package de.olympia.main.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TranslationService {

    public static final String DEFAULT_LANG = "en";

    private static final Map<String, Map<String, String>> SPORT_TRANSLATIONS = Map.ofEntries(
        entry("Alpine Skiing",              "Alpine Skiing",            "Ski Alpin",                "Ski alpin"),
        entry("Biathlon",                   "Biathlon",                 "Biathlon",                 "Biathlon"),
        entry("Cross-Country Skiing",       "Cross-Country Skiing",    "Skilanglauf",              "Ski de fond"),
        entry("Ski Jumping",                "Ski Jumping",              "Skispringen",              "Saut à ski"),
        entry("Nordic Combined",            "Nordic Combined",          "Nordische Kombination",    "Combiné nordique"),
        entry("Freestyle Skiing",           "Freestyle Skiing",         "Freestyle-Skiing",         "Ski acrobatique"),
        entry("Snowboarding",              "Snowboarding",             "Snowboarden",              "Snowboard"),
        entry("Figure Skating",             "Figure Skating",           "Eiskunstlauf",             "Patinage artistique"),
        entry("Speed Skating",              "Speed Skating",            "Eisschnelllauf",           "Patinage de vitesse"),
        entry("Short Track Speed Skating",  "Short Track Speed Skating","Shorttrack",               "Patinage de vitesse sur piste courte"),
        entry("Ice Hockey",                 "Ice Hockey",               "Eishockey",                "Hockey sur glace"),
        entry("Curling",                    "Curling",                  "Curling",                  "Curling"),
        entry("Bobsleigh",                  "Bobsleigh",                "Bobfahren",                "Bobsleigh"),
        entry("Skeleton",                   "Skeleton",                 "Skeleton",                 "Skeleton"),
        entry("Luge",                       "Luge",                     "Rodeln",                   "Luge")
    );

    private static final Map<String, Map<String, String>> MEDAL_TRANSLATIONS = Map.of(
        "GOLD",   Map.of("en", "Gold",   "de", "Gold",   "fr", "Or"),
        "SILVER", Map.of("en", "Silver", "de", "Silber", "fr", "Argent"),
        "BRONZE", Map.of("en", "Bronze", "de", "Bronze", "fr", "Bronze")
    );

    private static final Map<String, Map<String, String>> SCORE_TYPE_TRANSLATIONS = Map.of(
        "TIME", Map.of("en", "Time", "de", "Zeit",   "fr", "Temps"),
        "PTS",  Map.of("en", "Points", "de", "Punkte", "fr", "Points"),
        "WINS", Map.of("en", "Wins", "de", "Siege",  "fr", "Victoires")
    );

    private static final Map<String, Map<String, String>> COUNTRY_TRANSLATIONS = Map.ofEntries(
        entry("United States",      "United States",        "Vereinigte Staaten",       "États-Unis"),
        entry("Germany",            "Germany",              "Deutschland",              "Allemagne"),
        entry("France",             "France",               "Frankreich",               "France"),
        entry("Norway",             "Norway",               "Norwegen",                 "Norvège"),
        entry("Sweden",             "Sweden",               "Schweden",                 "Suède"),
        entry("Canada",             "Canada",               "Kanada",                   "Canada"),
        entry("Austria",            "Austria",              "Österreich",               "Autriche"),
        entry("Switzerland",        "Switzerland",          "Schweiz",                  "Suisse"),
        entry("Italy",              "Italy",                "Italien",                  "Italie"),
        entry("Japan",              "Japan",                "Japan",                    "Japon"),
        entry("China",              "China",                "China",                    "Chine"),
        entry("South Korea",        "South Korea",          "Südkorea",                 "Corée du Sud"),
        entry("Russia",             "Russia",               "Russland",                 "Russie"),
        entry("Finland",            "Finland",              "Finnland",                 "Finlande"),
        entry("Netherlands",        "Netherlands",          "Niederlande",              "Pays-Bas"),
        entry("Czech Republic",     "Czech Republic",       "Tschechien",               "République tchèque"),
        entry("Poland",             "Poland",               "Polen",                    "Pologne"),
        entry("Great Britain",      "Great Britain",        "Großbritannien",           "Grande-Bretagne"),
        entry("Spain",              "Spain",                "Spanien",                  "Espagne"),
        entry("Australia",          "Australia",            "Australien",               "Australie")
    );

    /**
     * Helper to build a translation entry: key -> {en, de, fr}
     */
    private static Map.Entry<String, Map<String, String>> entry(String key, String en, String de, String fr) {
        return Map.entry(key, Map.of("en", en, "de", de, "fr", fr));
    }

    public String normalizeLang(String lang) {
        if (lang == null) return DEFAULT_LANG;
        String lower = lang.toLowerCase().trim();
        if (lower.equals("de") || lower.equals("fr") || lower.equals("en")) {
            return lower;
        }
        return DEFAULT_LANG;
    }

    public String translateSport(String sportName, String lang) {
        return translate(SPORT_TRANSLATIONS, sportName, lang);
    }

    public String translateCountry(String countryName, String lang) {
        return translate(COUNTRY_TRANSLATIONS, countryName, lang);
    }

    public String translateMedal(String medal, String lang) {
        return translate(MEDAL_TRANSLATIONS, medal, lang);
    }

    public String translateScoreType(String scoreType, String lang) {
        return translate(SCORE_TYPE_TRANSLATIONS, scoreType, lang);
    }

    private String translate(Map<String, Map<String, String>> translations, String key, String lang) {
        if (key == null) return null;
        Map<String, String> langMap = translations.get(key);
        if (langMap == null) return key; // fallback: return original
        return langMap.getOrDefault(lang, key);
    }
}


