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
     * Returns all athletes enriched with medal counts, leaderboard rank and per-sport result sub-lists.
     * 
     * This method:
     * 1. Fetches all Result entities with their athlete and sport associations loaded
     * 2. Groups results by athlete for efficient access during DTO building
     * 3. Builds V2AthleteResponse DTOs for each athlete in the system
     * 4. Assigns leaderboard ranks based on medal counts (GOLD desc, SILVER desc, BRONZE desc)
     * 5. Sorts the final list by leaderboard rank
     * 6. Returns data cached by language code to reduce database queries
     *
     * The response includes:
     * - Basic athlete info (ID, first/last name)
     * - Country reference with language-specific translations
     * - Medal summary (gold, silver, bronze, total count)
     * - Per-sport result details sorted by rank
     * - Leaderboard rank (1 = best)
     *
     * @param lang ISO language code (en, de, fr) for translation resolution in country/sport names
     * @return Sorted list of V2AthleteResponse DTOs sorted by leaderboard rank (ascending)
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
     * Returns all countries with aggregated medal counts for the country and for each of its athletes.
     * 
     * This method:
     * 1. Fetches all Result entities with athlete and sport associations loaded
     * 2. Groups results by athlete for efficient access during country-level aggregation
     * 3. Maps athletes to their countries for country-athlete relationship lookup
     * 4. Builds V2CountryResponse DTOs for each country with athlete lists and medal counts
     * 5. Assigns leaderboard ranks based on country total medal counts
     * 6. Sorts the final list by leaderboard rank
     * 7. Returns data cached by language code to reduce database queries
     *
     * The response includes:
     * - Basic country info (ID, code, name with translations)
     * - Country-level medal summary (gold, silver, bronze, total)
     * - Athlete references for each athlete in the country (with their individual medal counts)
     * - Leaderboard rank (1 = best country by medal count)
     *
     * @param lang ISO language code (en, de, fr) for translation resolution in country/sport names
     * @return Sorted list of V2CountryResponse DTOs sorted by leaderboard rank (ascending)
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
     * Returns all sports with their participant lists sorted by rank.
     * 
     * This method:
     * 1. Fetches all Result entities with athlete and sport associations loaded
     * 2. Groups results by sport for efficient access during DTO building
     * 3. Builds V2SportResponse DTOs for each sport with participant lists
     * 4. Sorts participants within each sport by rank (lower rank = better placement)
     * 5. Sorts the final sport list alphabetically by sport name
     * 6. Returns data cached by language code to reduce database queries
     *
     * The response includes:
     * - Basic sport info (ID, name with translations)
     * - Score type (PTS, WINS, TIME) for display formatting
     * - Participant entries with:
     *   - Athlete information (name, country)
     *   - Result details (medal, time/points, rank)
     *   - Country reference with language-specific name translation
     *
     * @param lang ISO language code (en, de, fr) for translation resolution in country/sport names
     * @return Sorted list of V2SportResponse DTOs sorted by sport name (alphabetically)
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
     * Returns a single combined payload with sports, athletes and countries (V2LeaderboardResponse).
     * 
     * This is an optimization endpoint that allows the frontend to populate all views
     * without additional requests. Instead of making 3 separate API calls, clients can
     * retrieve all data in a single call, reducing network overhead and improving performance.
     *
     * This method:
     * 1. Fetches all Result entities once and shares them across all three builders
     * 2. Builds V2SportResponse list with all sports and participants
     * 3. Builds V2AthleteResponse list with all athletes and their results
     * 4. Builds V2CountryResponse list with all countries and their athletes
     * 5. Assigns leaderboard ranks for athletes and countries
     * 6. Sorts all lists appropriately (athletes/countries by rank, sports by name)
     * 7. Returns data cached by language code to reduce database queries
     *
     * Data efficiency: All results are loaded ONCE from the database and shared
     * across athletes, countries, and sports to minimize query overhead.
     *
     * @param lang ISO language code (en, de, fr) for translation resolution in country/sport names
     * @return V2LeaderboardResponse containing three complete leaderboard lists ready for display
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

    /**
     * Builds a V2AthleteResponse DTO from an athlete entity and their associated results.
     * 
     * This method:
     * 1. Aggregates medal counts from all results for the athlete
     * 2. Extracts and sorts per-sport result details
     * 3. Resolves country references with translations
     * 4. Initializes the leaderboard rank (assigned later in batch)
     *
     * @param athlete The athlete entity to build from
     * @param results All Result entities for this athlete (from database query)
     * @param lang ISO language code (en, de, fr) for translation resolution
     * @return A complete V2AthleteResponse DTO ready for serialization
     */
    private V2AthleteResponse buildAthleteResponse(Athlete athlete, List<Result> results, String lang) {
        int gold = 0, silver = 0, bronze = 0;
        List<V2AthleteResponse.SportResult> sportResults = new ArrayList<>();

        // Count medals and collect per-sport results
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

        // Sort sports results by rank
        sportResults.sort(Comparator.comparingInt(s -> s.getRank() != null ? s.getRank() : Integer.MAX_VALUE));

        V2AthleteResponse.MedalSummary medals = new V2AthleteResponse.MedalSummary(gold, silver, bronze, gold + silver + bronze);

        // Resolve country reference with language-specific name
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

    /**
     * Builds a V2CountryResponse DTO from a country entity and its associated athletes/results.
     * 
     * This method:
     * 1. Aggregates total medal counts across all country athletes
     * 2. Creates AthleteRef DTOs for each athlete with their individual medal counts
     * 3. Sorts athletes by total medals (descending)
     * 4. Resolves country translations for the requested language
     * 5. Initializes the leaderboard rank (assigned later in batch)
     *
     * @param country The country entity to build from
     * @param athletes List of all athletes belonging to this country
     * @param byAthlete Map of athlete ID -> their result list (cached from database query)
     * @param lang ISO language code (en, de, fr) for translation resolution
     * @return A complete V2CountryResponse DTO with all athletes and medal counts
     */
    private V2CountryResponse buildCountryResponse(Country country, List<Athlete> athletes,
                                                    Map<Long, List<Result>> byAthlete, String lang) {
        int gold = 0, silver = 0, bronze = 0;
        List<V2CountryResponse.AthleteRef> athleteRefs = new ArrayList<>();

        // Iterate through athletes and accumulate medals
        for (Athlete a : athletes) {
            List<Result> results = byAthlete.getOrDefault(a.getId(), List.of());
            int ag = 0, as_ = 0, ab = 0;
            // Count this athlete's medals
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

        // Sort athletes by total medals (descending) for display
        athleteRefs.sort(Comparator.comparingInt((V2CountryResponse.AthleteRef a) -> a.getMedals().getTotal()).reversed());
        V2CountryResponse.MedalSummary summary = new V2CountryResponse.MedalSummary(gold, silver, bronze, gold + silver + bronze);

        return new V2CountryResponse(
                country.getId(),
                country.getCode(),
                resolveCountryName(country, lang),
                country.getNameEn(),
                country.getNameDe(),
                country.getNameFr(),
                summary,
                null, // rank assigned after all countries are built
                athleteRefs
        );
    }

    /**
     * Builds a V2SportResponse DTO from a sport entity and its associated results.
     * 
     * This method:
     * 1. Transforms each result into a ParticipantEntry with athlete and country info
     * 2. Sorts participants by rank (typically medal positions)
     * 3. Resolves country names with language-specific translations
     * 4. Includes score type information for display formatting
     *
     * @param sport The sport entity to build from
     * @param results List of all Result entities for this sport (with athlete/sport loaded)
     * @param lang ISO language code (en, de, fr) for translation resolution
     * @return A complete V2SportResponse DTO with all participants sorted by rank
     */
    private V2SportResponse buildSportResponse(Sports sport, List<Result> results, String lang) {
        List<V2SportResponse.ParticipantEntry> participants = results.stream()
                .map(r -> {
                    String countryCode = null;
                    String countryName = null;
                    Long countryId = null;
                    // Resolve country reference if athlete has a country
                    if (r.getAthlete().getCountry() != null) {
                        countryId = r.getAthlete().getCountry().getId();
                        countryCode = r.getAthlete().getCountry().getCode();
                        countryName = resolveCountryName(r.getAthlete().getCountry(), lang);
                    }
                    return new V2SportResponse.ParticipantEntry(
                            r.getAthlete().getId(),
                            r.getAthlete().getFirstName(),
                            r.getAthlete().getLastName(),
                            countryId,
                            countryCode,
                            countryName,
                            r.getMedal() != null ? r.getMedal().name() : null,
                            r.getTimeOrPoints(),
                            r.getRank(),
                            r.getId()
                    );
                })
                // Sort by rank (lower rank = better performance)
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
     * Assigns leaderboard ranks to athletes in-place based on medal counts.
     * 
     * Ranking order: GOLD (descending) → SILVER (descending) → BRONZE (descending) → name (ascending)
     * This method modifies the input list directly, setting the leaderboardRank field
     * on each athlete response in order of their ranking position (1 = best).
     *
     * @param athletes Mutable list of V2AthleteResponse objects to rank
     */
    private void assignAthleteRanks(List<V2AthleteResponse> athletes) {
        // Sort by medal counts (gold desc, silver desc, bronze desc)
        List<V2AthleteResponse> sorted = athletes.stream()
                .sorted(medalComparator(
                        (V2AthleteResponse a) -> a.getMedals().getGold(),
                        (V2AthleteResponse a) -> a.getMedals().getSilver(),
                        (V2AthleteResponse a) -> a.getMedals().getBronze()))
                .collect(Collectors.toList());

        // Assign ranks 1..N
        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).setLeaderboardRank(i + 1);
        }
    }

    /**
     * Assigns leaderboard ranks to countries in-place based on medal counts.
     * 
     * Uses same ranking order as athletes: GOLD (desc) → SILVER (desc) → BRONZE (desc)
     * This method modifies the input list directly, setting the leaderboardRank field
     * on each country response in order of their ranking position (1 = best).
     *
     * @param countries Mutable list of V2CountryResponse objects to rank
     */
    private void assignCountryRanks(List<V2CountryResponse> countries) {
        // Sort by medal counts (gold desc, silver desc, bronze desc)
        List<V2CountryResponse> sorted = countries.stream()
                .sorted(medalComparator(
                        (V2CountryResponse c) -> c.getMedals().getGold(),
                        (V2CountryResponse c) -> c.getMedals().getSilver(),
                        (V2CountryResponse c) -> c.getMedals().getBronze()))
                .collect(Collectors.toList());

        // Assign ranks 1..N
        for (int i = 0; i < sorted.size(); i++) {
            sorted.get(i).setLeaderboardRank(i + 1);
        }
    }

    /**
     * Generic medal-based comparator used for both athletes and countries.
     * 
     * Sorts by: Gold count (desc) → Silver count (desc) → Bronze count (desc)
     * This ensures consistent ranking across all leaderboard views.
     * The comparators are flexible and accept lambda functions that extract
     * the medal counts from either athlete or country response objects.
     *
     * @param goldFn Function to extract gold medal count from object T
     * @param silverFn Function to extract silver medal count from object T
     * @param bronzeFn Function to extract bronze medal count from object T
     * @return A Comparator that sorts objects by medal counts in descending order
     */
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

    /**
     * Resolves the localized name for a sport based on the requested language.
     * 
     * Falls back to the English translation if the requested language translation is null,
     * and falls back to the default sport name if English translation is also null.
     * Supports: English (en), German (de), French (fr).
     *
     * @param sport The Sports entity with translated names
     * @param lang ISO language code: "en", "de", "fr", or unknown (defaults to English)
     * @return The translated sport name, or the default sport name if no translation exists
     */
    private String resolveSportName(Sports sport, String lang) {
        return switch (lang) {
            case "de" -> sport.getNameDe() != null ? sport.getNameDe() : sport.getName();
            case "fr" -> sport.getNameFr() != null ? sport.getNameFr() : sport.getName();
            default   -> sport.getNameEn() != null ? sport.getNameEn() : sport.getName();
        };
    }

    /**
     * Resolves the localized name for a country based on the requested language.
     * 
     * Falls back to the English translation if the requested language translation is null,
     * and falls back to the default country name if English translation is also null.
     * Supports: English (en), German (de), French (fr).
     *
     * @param country The Country entity with translated names
     * @param lang ISO language code: "en", "de", "fr", or unknown (defaults to English)
     * @return The translated country name, or the default country name if no translation exists
     */
    private String resolveCountryName(Country country, String lang) {
        return switch (lang) {
            case "de" -> country.getNameDe() != null ? country.getNameDe() : country.getName();
            case "fr" -> country.getNameFr() != null ? country.getNameFr() : country.getName();
            default   -> country.getNameEn() != null ? country.getNameEn() : country.getName();
        };
    }
}



