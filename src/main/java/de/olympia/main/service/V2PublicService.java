package de.olympia.main.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import de.olympia.main.dto.v2.V2AthleteResponse;
import de.olympia.main.dto.v2.V2CountryResponse;
import de.olympia.main.dto.v2.V2LeaderboardResponse;
import de.olympia.main.dto.v2.V2SportResponse;
import de.olympia.main.entity.Athlete;
import de.olympia.main.entity.Country;
import de.olympia.main.entity.Result;
import de.olympia.main.entity.Sports;
import de.olympia.main.repository.AthleteRepository;
import de.olympia.main.repository.CountryRepository;
import de.olympia.main.repository.ResultRepository;
import de.olympia.main.repository.SportsRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class V2PublicService {

    private final AthleteRepository athleteRepository;
    private final CountryRepository countryRepository;
    private final SportsRepository sportsRepository;
    private final ResultRepository resultRepository;

    /**
     * Returns all athletes enriched with medal counts, leaderboard rank and
     * a per-sport result sub-list.
     *
     * @param lang ISO language code (en, de, fr)
     * @return sorted list of V2AthleteResponse DTOs
     */
    @Cacheable(value = "v2Athletes", key = "#lang")
    @Transactional(readOnly = true)
    public List<V2AthleteResponse> getAthletes(String lang) {
        log.info("V2: fetching athletes (lang={})", lang);

        List<Result> allResults = resultRepository.findAllWithAthleteAndSport();
        Map<Long, List<Result>> byAthlete = allResults.stream()
                .collect(Collectors.groupingBy(r -> r.getAthlete().getId()));

        List<Athlete> athletes = athleteRepository.findAll();

        List<V2AthleteResponse> responses = athletes.stream()
                .map(a -> buildAthleteResponse(a, byAthlete.getOrDefault(a.getId(), List.of()), lang))
                .sorted(Comparator.comparingInt((V2AthleteResponse r) ->
                        r.getLeaderboardRank() != null ? r.getLeaderboardRank() : Integer.MAX_VALUE))
                .collect(Collectors.toList());

        assignAthleteRanks(responses);
        return responses;
    }

    /**
     * Returns all countries with aggregated medal counts for the country and
     * for each of its athletes.
     *
     * @param lang ISO language code (en, de, fr)
     * @return sorted list of V2CountryResponse DTOs
     */
    @Cacheable(value = "v2Countries", key = "#lang")
    @Transactional(readOnly = true)
    public List<V2CountryResponse> getCountries(String lang) {
        log.info("V2: fetching countries (lang={})", lang);

        List<Result> allResults = resultRepository.findAllWithAthleteAndSport();
        Map<Long, List<Result>> byAthlete = allResults.stream()
                .collect(Collectors.groupingBy(r -> r.getAthlete().getId()));

        List<Country> countries = countryRepository.findAll();
        List<Athlete> athletes = athleteRepository.findAll();
        Map<Long, List<Athlete>> athletesByCountry = athletes.stream()
                .filter(a -> a.getCountry() != null)
                .collect(Collectors.groupingBy(a -> a.getCountry().getId()));

        List<V2CountryResponse> responses = countries.stream()
                .map(c -> buildCountryResponse(c, athletesByCountry.getOrDefault(c.getId(), List.of()), byAthlete, lang))
                .collect(Collectors.toList());

        assignCountryRanks(responses);

        return responses.stream()
                .sorted(Comparator.comparingInt((V2CountryResponse r) ->
                        r.getLeaderboardRank() != null ? r.getLeaderboardRank() : Integer.MAX_VALUE))
                .collect(Collectors.toList());
    }

    /**
     * Returns all sports with their participant list sorted by rank.
     *
     * @param lang ISO language code (en, de, fr)
     * @return sorted list of V2SportResponse DTOs
     */
    @Cacheable(value = "v2Sports", key = "#lang")
    @Transactional(readOnly = true)
    public List<V2SportResponse> getSports(String lang) {
        log.info("V2: fetching sports (lang={})", lang);

        List<Result> allResults = resultRepository.findAllWithAthleteAndSport();
        Map<Long, List<Result>> bySport = allResults.stream()
                .filter(r -> r.getSports() != null)
                .collect(Collectors.groupingBy(r -> r.getSports().getId()));

        return sportsRepository.findAll().stream()
                .map(s -> buildSportResponse(s, bySport.getOrDefault(s.getId(), List.of()), lang))
                .sorted(Comparator.comparing(V2SportResponse::getName))
                .collect(Collectors.toList());
    }

    /**
     * Returns a single combined payload with sports, athletes and countries.
     * Allows the frontend to populate all views without additional requests in some cases.
     *
     * @param lang ISO language code (en, de, fr)
     * @return V2LeaderboardResponse containing all three lists
     */
    @Cacheable(value = "v2Leaderboard", key = "#lang")
    @Transactional(readOnly = true)
    public V2LeaderboardResponse getLeaderboard(String lang) {
        log.info("V2: fetching leaderboard (lang={})", lang);

        // Load all results once and share across builders
        List<Result> allResults = resultRepository.findAllWithAthleteAndSport();
        Map<Long, List<Result>> byAthlete = allResults.stream()
                .collect(Collectors.groupingBy(r -> r.getAthlete().getId()));
        Map<Long, List<Result>> bySport = allResults.stream()
                .filter(r -> r.getSports() != null)
                .collect(Collectors.groupingBy(r -> r.getSports().getId()));

        // Sports
        List<V2SportResponse> sports = sportsRepository.findAll().stream()
                .map(s -> buildSportResponse(s, bySport.getOrDefault(s.getId(), List.of()), lang))
                .sorted(Comparator.comparing(V2SportResponse::getName))
                .collect(Collectors.toList());

        // Athletes
        List<V2AthleteResponse> athletes = athleteRepository.findAll().stream()
                .map(a -> buildAthleteResponse(a, byAthlete.getOrDefault(a.getId(), List.of()), lang))
                .collect(Collectors.toList());
        assignAthleteRanks(athletes);
        athletes.sort(Comparator.comparingInt(r -> r.getLeaderboardRank() != null ? r.getLeaderboardRank() : Integer.MAX_VALUE));

        // Countries
        List<Athlete> allAthletes = athleteRepository.findAll();
        Map<Long, List<Athlete>> athletesByCountry = allAthletes.stream()
                .filter(a -> a.getCountry() != null)
                .collect(Collectors.groupingBy(a -> a.getCountry().getId()));
        List<V2CountryResponse> countries = countryRepository.findAll().stream()
                .map(c -> buildCountryResponse(c, athletesByCountry.getOrDefault(c.getId(), List.of()), byAthlete, lang))
                .collect(Collectors.toList());
        assignCountryRanks(countries);
        countries.sort(Comparator.comparingInt(r -> r.getLeaderboardRank() != null ? r.getLeaderboardRank() : Integer.MAX_VALUE));

        return new V2LeaderboardResponse(sports, athletes, countries);
    }

    // -----------------------------------------------------------------
    // Private builders
    // -----------------------------------------------------------------

    private V2AthleteResponse buildAthleteResponse(Athlete athlete, List<Result> results, String lang) {
        int gold = 0, silver = 0, bronze = 0;
        List<V2AthleteResponse.SportResult> sportResults = new ArrayList<>();

        for (Result r : results) {
            if (r.getMedal() != null) {
                switch (r.getMedal()) {
                    case GOLD   -> gold++;
                    case SILVER -> silver++;
                    case BRONZE -> bronze++;
                }
            }

            if (r.getSports() != null) {
                sportResults.add(new V2AthleteResponse.SportResult(
                        r.getSports().getId(),
                        resolveSportName(r.getSports(), lang),
                        r.getSports().getName(),
                        r.getSports().getScoreType() != null ? r.getSports().getScoreType().name() : null,
                        r.getTimeOrPoints(),
                        r.getRank(),
                        r.getMedal() != null ? r.getMedal().name() : null
                ));
            }
        }

        sportResults.sort(Comparator.comparingInt(s -> s.getRank() != null ? s.getRank() : Integer.MAX_VALUE));

        V2AthleteResponse.MedalSummary medals = new V2AthleteResponse.MedalSummary(gold, silver, bronze, gold + silver + bronze);

        V2AthleteResponse.CountryRef countryRef = null;
        if (athlete.getCountry() != null) {
            Country c = athlete.getCountry();
            countryRef = new V2AthleteResponse.CountryRef(c.getId(), c.getCode(), resolveCountryName(c, lang));
        }

        return new V2AthleteResponse(
                athlete.getId(),
                athlete.getFirstName(),
                athlete.getLastName(),
                countryRef,
                medals,
                null, // rank will be assigned after all athletes are built
                sportResults
        );
    }

    private V2CountryResponse buildCountryResponse(Country country, List<Athlete> athletes,
                                                    Map<Long, List<Result>> byAthlete, String lang) {
        int gold = 0, silver = 0, bronze = 0;
        List<V2CountryResponse.AthleteRef> athleteRefs = new ArrayList<>();

        for (Athlete a : athletes) {
            List<Result> results = byAthlete.getOrDefault(a.getId(), List.of());
            int ag = 0, as_ = 0, ab = 0;
            for (Result r : results) {
                if (r.getMedal() != null) {
                    switch (r.getMedal()) {
                        case GOLD   -> { ag++; gold++; }
                        case SILVER -> { as_++; silver++; }
                        case BRONZE -> { ab++; bronze++; }
                    }
                }
            }
            athleteRefs.add(new V2CountryResponse.AthleteRef(
                    a.getId(), a.getFirstName(), a.getLastName(),
                    new V2CountryResponse.MedalSummary(ag, as_, ab, ag + as_ + ab)
            ));
        }

        // Sort athletes by total medals descending
        athleteRefs.sort(Comparator.comparingInt((V2CountryResponse.AthleteRef a) -> a.getMedals().getTotal()).reversed());
        V2CountryResponse.MedalSummary summary = new V2CountryResponse.MedalSummary(gold, silver, bronze, gold + silver + bronze);

        return new V2CountryResponse(
                country.getId(),
                country.getCode(),
                resolveCountryName(country, lang),
                summary,
                null, // rank assigned after all countries are built
                athleteRefs
        );
    }

    private V2SportResponse buildSportResponse(Sports sport, List<Result> results, String lang) {
        List<V2SportResponse.ParticipantEntry> participants = results.stream()
                .map(r -> {
                    String countryCode = null;
                    String countryName = null;
                    if (r.getAthlete().getCountry() != null) {
                        countryCode = r.getAthlete().getCountry().getCode();
                        countryName = resolveCountryName(r.getAthlete().getCountry(), lang);
                    }
                    return new V2SportResponse.ParticipantEntry(
                            r.getAthlete().getId(),
                            r.getAthlete().getFirstName(),
                            r.getAthlete().getLastName(),
                            countryCode,
                            countryName,
                            r.getMedal() != null ? r.getMedal().name() : null,
                            r.getTimeOrPoints(),
                            r.getRank()
                    );
                })
                .sorted(Comparator.comparingInt(p -> p.getRank() != null ? p.getRank() : Integer.MAX_VALUE))
                .collect(Collectors.toList());

        return new V2SportResponse(
                sport.getId(),
                sport.getName(),
                resolveSportName(sport, lang),
                sport.getScoreType() != null ? sport.getScoreType().name() : "TIME",
                participants
        );
    }

    // -----------------------------------------------------------------
    // Ranking helpers
    // -----------------------------------------------------------------

    /**
     * Assigns leaderboard ranks to athletes in-place.
     * Rank is determined by: GOLD desc, SILVER desc, BRONZE desc, name asc.
     */
    private void assignAthleteRanks(List<V2AthleteResponse> athletes) {
        List<V2AthleteResponse> sorted = athletes.stream()
                .sorted(medalComparator(
                        (V2AthleteResponse a) -> a.getMedals().getGold(),
                        (V2AthleteResponse a) -> a.getMedals().getSilver(),
                        (V2AthleteResponse a) -> a.getMedals().getBronze()))
                .collect(Collectors.toList());

        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).setLeaderboardRank(i + 1);
        }
    }

    private void assignCountryRanks(List<V2CountryResponse> countries) {
        List<V2CountryResponse> sorted = countries.stream()
                .sorted(medalComparator(
                        (V2CountryResponse c) -> c.getMedals().getGold(),
                        (V2CountryResponse c) -> c.getMedals().getSilver(),
                        (V2CountryResponse c) -> c.getMedals().getBronze()))
                .collect(Collectors.toList());

        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).setLeaderboardRank(i + 1);
        }
    }

    /** Generic comparator: sort by gold desc, then silver desc, then bronze desc. */
    private <T> Comparator<T> medalComparator(
            java.util.function.ToIntFunction<T> goldFn,
            java.util.function.ToIntFunction<T> silverFn,
            java.util.function.ToIntFunction<T> bronzeFn) {
        return Comparator
                .comparingInt(goldFn).reversed()
                .thenComparing(Comparator.comparingInt(silverFn).reversed())
                .thenComparing(Comparator.comparingInt(bronzeFn).reversed());
    }

    // -----------------------------------------------------------------
    // Translation helpers
    // -----------------------------------------------------------------

    private String resolveSportName(Sports sport, String lang) {
        return switch (lang) {
            case "de" -> sport.getNameDe() != null ? sport.getNameDe() : sport.getName();
            case "fr" -> sport.getNameFr() != null ? sport.getNameFr() : sport.getName();
            default   -> sport.getNameEn() != null ? sport.getNameEn() : sport.getName();
        };
    }

    private String resolveCountryName(Country country, String lang) {
        return switch (lang) {
            case "de" -> country.getNameDe() != null ? country.getNameDe() : country.getName();
            case "fr" -> country.getNameFr() != null ? country.getNameFr() : country.getName();
            default   -> country.getNameEn() != null ? country.getNameEn() : country.getName();
        };
    }
}



