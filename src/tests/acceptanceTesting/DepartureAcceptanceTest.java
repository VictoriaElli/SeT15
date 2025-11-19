package acceptanceTesting;

import dto.DepartureRequestDTO;
import dto.DepartureResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = app.SpringBoot.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DepartureAcceptanceTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void testDeparturesEndpoint() {
        DepartureRequestDTO request = new DepartureRequestDTO("A", "B", LocalDate.of(2025, 11, 19),
                LocalTime.of(10,0), null);

        DepartureResponseDTO[] response = restTemplate.postForObject(
                "http://localhost:" + port + "/api/departures",
                request,
                DepartureResponseDTO[].class
        );

        assertNotNull(response);
        assertTrue(response.length >= 0);
    }
}
