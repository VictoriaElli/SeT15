package usecases;

import domain.model.*;
import org.springframework.stereotype.Service;
import port.outbound.ExceptionEntryRepository;
import port.outbound.FrequencyRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleService {

    private final FrequencyRepository frequencyRepo;
    private final ExceptionEntryRepository exceptionRepo;

    public ScheduleService(FrequencyRepository frequencyRepo,
                           ExceptionEntryRepository exceptionRepo) {
        this.frequencyRepo = frequencyRepo;
        this.exceptionRepo = exceptionRepo;
    }

    // =========================
    // Rutetidsgenerering
    // =========================

    // Genererer alle avgangstider for en gitt rute, stopp og dato.
    // Tar hensyn til både frekvenser og eventuelle unntak.
    public List<LocalTime> generateTimesForDate(Route route, Stop stop, LocalDate date) {
        List<LocalTime> times = new ArrayList<>();

        // Hent frekvenser
        List<Frequency> frequencies = frequencyRepo.findByRouteAndStop(route, stop);
        for (Frequency freq : frequencies) {
            times.addAll(generateTimes(freq, date));
        }

        // Hent eventuelle unntak
        List<ExceptionEntry> exceptions = exceptionRepo.findByRouteStopAndDate(route, stop, date);
        for (ExceptionEntry ex : exceptions) {
            if (ex.isCancelled()) {
                times.remove(ex.getDepartureTime());
            } else {
                times.add(ex.getDepartureTime());
            }
        }

        times.sort(LocalTime::compareTo);
        return times;
    }

    // Genererer tider basert på én Frequency.
    public List<LocalTime> generateTimes(Frequency frequency, LocalDate date) {
        List<LocalTime> times = new ArrayList<>();

        if (!frequency.getSeason().isActiveOn(date) ||
                frequency.getWeekday() != Weekday.fromLocalDate(date)) {
            return times; // tom liste hvis frekvensen ikke gjelder
        }

        LocalTime current = frequency.getFirstDeparture();
        while (!current.isAfter(frequency.getLastDeparture())) {
            times.add(current);
            current = current.plusMinutes(frequency.getIntervalMinutes());
        }

        return times;
    }


    // =========================
    // CRUD-operasjoner
    // =========================

    // Frequency
    public Frequency updateFrequency(Frequency freq) {
        return frequencyRepo.save(freq);
    }

    public void deleteFrequency(int id) {
        frequencyRepo.deleteById(id);
    }

    // ExceptionEntry
    public ExceptionEntry updateException(ExceptionEntry entry) {
        return exceptionRepo.save(entry);
    }

    public void deleteException(int id) {
        exceptionRepo.deleteById(id);
    }
}
