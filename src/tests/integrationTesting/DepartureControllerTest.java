package integrationTesting;

import controllers.DepartureController;
import dto.DepartureRequestDTO;
import dto.DepartureResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import service.ScheduleService;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest(classes = app.SpringBoot.class)
@AutoConfigureMockMvc
class DepartureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ScheduleService scheduleService;

    @Autowired
    private ObjectMapper objectMapper; // For å konvertere DTO til JSON

    @Test
    void testGetDepartures_returnsResponse() throws Exception {
        // Mock respons fra ScheduleService
        DepartureResponseDTO mockResponse = new DepartureResponseDTO();
        mockResponse.setRouteNumber(1);
        mockResponse.setFromStopName("Gamlebyen");
        mockResponse.setToStopName("Ålekilen");
        mockResponse.setTravelDate(LocalDate.of(2025, 11, 19));
        mockResponse.setPlannedDeparture(LocalTime.of(10, 0));
        mockResponse.setArrivalTime(LocalTime.of(10, 30));

        when(scheduleService.getDepartures(any(DepartureRequestDTO.class)))
                .thenReturn(List.of(mockResponse));

        // Bygg request
        DepartureRequestDTO requestDTO = new DepartureRequestDTO(
                "Gamlebyen", "Ålekilen",
                LocalDate.of(2025, 11, 19),
                LocalTime.of(10, 0),
                null
        );

        mockMvc.perform(post("/api/departures/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].routeNumber").value(1))
                .andExpect(jsonPath("$[0].fromStopName").value("Gamlebyen"))
                .andExpect(jsonPath("$[0].toStopName").value("Ålekilen"))
                .andExpect(jsonPath("$[0].plannedDeparture").value("10:00:00"))
                .andExpect(jsonPath("$[0].arrivalTime").value("10:30:00"));
    }

    @Test
    void testGetDepartures_emptyResponse() throws Exception {
        when(scheduleService.getDepartures(any(DepartureRequestDTO.class)))
                .thenReturn(List.of());

        DepartureRequestDTO requestDTO = new DepartureRequestDTO(
                "Lisleby", "Sellebakk",
                LocalDate.of(2025, 11, 19),
                LocalTime.of(10, 0),
                null
        );

        mockMvc.perform(post("/api/departures/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));
    }
}
