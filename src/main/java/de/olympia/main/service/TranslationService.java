package de.olympia.main.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
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

    private static final Map<String, Map<String, String>> COUNTRY_TRANSLATIONS;

    static {
        Map<String, Map<String, String>> m = new HashMap<>();
        // Europe – already existing
        m.put("United States",      Map.of("en", "United States",       "de", "Vereinigte Staaten",             "fr", "États-Unis"));
        m.put("Germany",            Map.of("en", "Germany",             "de", "Deutschland",                    "fr", "Allemagne"));
        m.put("France",             Map.of("en", "France",              "de", "Frankreich",                     "fr", "France"));
        m.put("Norway",             Map.of("en", "Norway",              "de", "Norwegen",                       "fr", "Norvège"));
        m.put("Sweden",             Map.of("en", "Sweden",              "de", "Schweden",                       "fr", "Suède"));
        m.put("Canada",             Map.of("en", "Canada",              "de", "Kanada",                         "fr", "Canada"));
        m.put("Austria",            Map.of("en", "Austria",             "de", "Österreich",                     "fr", "Autriche"));
        m.put("Switzerland",        Map.of("en", "Switzerland",         "de", "Schweiz",                        "fr", "Suisse"));
        m.put("Italy",              Map.of("en", "Italy",               "de", "Italien",                        "fr", "Italie"));
        m.put("Japan",              Map.of("en", "Japan",               "de", "Japan",                          "fr", "Japon"));
        m.put("China",              Map.of("en", "China",               "de", "China",                          "fr", "Chine"));
        m.put("South Korea",        Map.of("en", "South Korea",         "de", "Südkorea",                       "fr", "Corée du Sud"));
        m.put("Russia",             Map.of("en", "Russia",              "de", "Russland",                       "fr", "Russie"));
        m.put("Finland",            Map.of("en", "Finland",             "de", "Finnland",                       "fr", "Finlande"));
        m.put("Netherlands",        Map.of("en", "Netherlands",         "de", "Niederlande",                    "fr", "Pays-Bas"));
        m.put("Czech Republic",     Map.of("en", "Czech Republic",      "de", "Tschechien",                     "fr", "République tchèque"));
        m.put("Poland",             Map.of("en", "Poland",              "de", "Polen",                          "fr", "Pologne"));
        m.put("Great Britain",      Map.of("en", "Great Britain",       "de", "Großbritannien",                 "fr", "Grande-Bretagne"));
        m.put("Spain",              Map.of("en", "Spain",               "de", "Spanien",                        "fr", "Espagne"));
        m.put("Australia",          Map.of("en", "Australia",           "de", "Australien",                     "fr", "Australie"));
        // Europe – new
        m.put("Albania",            Map.of("en", "Albania",             "de", "Albanien",                       "fr", "Albanie"));
        m.put("Andorra",            Map.of("en", "Andorra",             "de", "Andorra",                        "fr", "Andorre"));
        m.put("Armenia",            Map.of("en", "Armenia",             "de", "Armenien",                       "fr", "Arménie"));
        m.put("Azerbaijan",         Map.of("en", "Azerbaijan",          "de", "Aserbaidschan",                  "fr", "Azerbaïdjan"));
        m.put("Belarus",            Map.of("en", "Belarus",             "de", "Weißrussland",                   "fr", "Biélorussie"));
        m.put("Belgium",            Map.of("en", "Belgium",             "de", "Belgien",                        "fr", "Belgique"));
        m.put("Bosnia and Herzegovina", Map.of("en", "Bosnia and Herzegovina", "de", "Bosnien und Herzegowina","fr", "Bosnie-Herzégovine"));
        m.put("Bulgaria",           Map.of("en", "Bulgaria",            "de", "Bulgarien",                      "fr", "Bulgarie"));
        m.put("Croatia",            Map.of("en", "Croatia",             "de", "Kroatien",                       "fr", "Croatie"));
        m.put("Cyprus",             Map.of("en", "Cyprus",              "de", "Zypern",                         "fr", "Chypre"));
        m.put("Denmark",            Map.of("en", "Denmark",             "de", "Dänemark",                       "fr", "Danemark"));
        m.put("Estonia",            Map.of("en", "Estonia",             "de", "Estland",                        "fr", "Estonie"));
        m.put("Georgia",            Map.of("en", "Georgia",             "de", "Georgien",                       "fr", "Géorgie"));
        m.put("Greece",             Map.of("en", "Greece",              "de", "Griechenland",                   "fr", "Grèce"));
        m.put("Hungary",            Map.of("en", "Hungary",             "de", "Ungarn",                         "fr", "Hongrie"));
        m.put("Iceland",            Map.of("en", "Iceland",             "de", "Island",                         "fr", "Islande"));
        m.put("Ireland",            Map.of("en", "Ireland",             "de", "Irland",                         "fr", "Irlande"));
        m.put("Israel",             Map.of("en", "Israel",              "de", "Israel",                         "fr", "Israël"));
        m.put("Kazakhstan",         Map.of("en", "Kazakhstan",          "de", "Kasachstan",                     "fr", "Kazakhstan"));
        m.put("Kosovo",             Map.of("en", "Kosovo",              "de", "Kosovo",                         "fr", "Kosovo"));
        m.put("Latvia",             Map.of("en", "Latvia",              "de", "Lettland",                       "fr", "Lettonie"));
        m.put("Liechtenstein",      Map.of("en", "Liechtenstein",       "de", "Liechtenstein",                  "fr", "Liechtenstein"));
        m.put("Lithuania",          Map.of("en", "Lithuania",           "de", "Litauen",                        "fr", "Lituanie"));
        m.put("Luxembourg",         Map.of("en", "Luxembourg",          "de", "Luxemburg",                      "fr", "Luxembourg"));
        m.put("Malta",              Map.of("en", "Malta",               "de", "Malta",                          "fr", "Malte"));
        m.put("Moldova",            Map.of("en", "Moldova",             "de", "Moldau",                         "fr", "Moldavie"));
        m.put("Monaco",             Map.of("en", "Monaco",              "de", "Monaco",                         "fr", "Monaco"));
        m.put("Montenegro",         Map.of("en", "Montenegro",          "de", "Montenegro",                     "fr", "Monténégro"));
        m.put("North Macedonia",    Map.of("en", "North Macedonia",     "de", "Nordmazedonien",                 "fr", "Macédoine du Nord"));
        m.put("Portugal",           Map.of("en", "Portugal",            "de", "Portugal",                       "fr", "Portugal"));
        m.put("Romania",            Map.of("en", "Romania",             "de", "Rumänien",                       "fr", "Roumanie"));
        m.put("San Marino",         Map.of("en", "San Marino",          "de", "San Marino",                     "fr", "Saint-Marin"));
        m.put("Serbia",             Map.of("en", "Serbia",              "de", "Serbien",                        "fr", "Serbie"));
        m.put("Slovakia",           Map.of("en", "Slovakia",            "de", "Slowakei",                       "fr", "Slovaquie"));
        m.put("Slovenia",           Map.of("en", "Slovenia",            "de", "Slowenien",                      "fr", "Slovénie"));
        m.put("Turkey",             Map.of("en", "Turkey",              "de", "Türkei",                         "fr", "Turquie"));
        m.put("Ukraine",            Map.of("en", "Ukraine",             "de", "Ukraine",                        "fr", "Ukraine"));
        // Asia
        m.put("Afghanistan",        Map.of("en", "Afghanistan",         "de", "Afghanistan",                    "fr", "Afghanistan"));
        m.put("Bahrain",            Map.of("en", "Bahrain",             "de", "Bahrain",                        "fr", "Bahreïn"));
        m.put("Bangladesh",         Map.of("en", "Bangladesh",          "de", "Bangladesch",                    "fr", "Bangladesh"));
        m.put("Bhutan",             Map.of("en", "Bhutan",              "de", "Bhutan",                         "fr", "Bhoutan"));
        m.put("Brunei",             Map.of("en", "Brunei",              "de", "Brunei",                         "fr", "Brunei"));
        m.put("Cambodia",           Map.of("en", "Cambodia",            "de", "Kambodscha",                     "fr", "Cambodge"));
        m.put("Hong Kong",          Map.of("en", "Hong Kong",           "de", "Hongkong",                       "fr", "Hong Kong"));
        m.put("India",              Map.of("en", "India",               "de", "Indien",                         "fr", "Inde"));
        m.put("Indonesia",          Map.of("en", "Indonesia",           "de", "Indonesien",                     "fr", "Indonésie"));
        m.put("Iran",               Map.of("en", "Iran",                "de", "Iran",                           "fr", "Iran"));
        m.put("Iraq",               Map.of("en", "Iraq",                "de", "Irak",                           "fr", "Irak"));
        m.put("Jordan",             Map.of("en", "Jordan",              "de", "Jordanien",                      "fr", "Jordanie"));
        m.put("Kuwait",             Map.of("en", "Kuwait",              "de", "Kuwait",                         "fr", "Koweït"));
        m.put("Kyrgyzstan",         Map.of("en", "Kyrgyzstan",          "de", "Kirgisistan",                    "fr", "Kirghizistan"));
        m.put("Laos",               Map.of("en", "Laos",                "de", "Laos",                           "fr", "Laos"));
        m.put("Lebanon",            Map.of("en", "Lebanon",             "de", "Libanon",                        "fr", "Liban"));
        m.put("Malaysia",           Map.of("en", "Malaysia",            "de", "Malaysia",                       "fr", "Malaisie"));
        m.put("Maldives",           Map.of("en", "Maldives",            "de", "Malediven",                      "fr", "Maldives"));
        m.put("Mongolia",           Map.of("en", "Mongolia",            "de", "Mongolei",                       "fr", "Mongolie"));
        m.put("Myanmar",            Map.of("en", "Myanmar",             "de", "Myanmar",                        "fr", "Myanmar"));
        m.put("Nepal",              Map.of("en", "Nepal",               "de", "Nepal",                          "fr", "Népal"));
        m.put("North Korea",        Map.of("en", "North Korea",         "de", "Nordkorea",                      "fr", "Corée du Nord"));
        m.put("Oman",               Map.of("en", "Oman",                "de", "Oman",                           "fr", "Oman"));
        m.put("Pakistan",           Map.of("en", "Pakistan",            "de", "Pakistan",                       "fr", "Pakistan"));
        m.put("Palestine",          Map.of("en", "Palestine",           "de", "Palästina",                      "fr", "Palestine"));
        m.put("Philippines",        Map.of("en", "Philippines",         "de", "Philippinen",                    "fr", "Philippines"));
        m.put("Qatar",              Map.of("en", "Qatar",               "de", "Katar",                          "fr", "Qatar"));
        m.put("Saudi Arabia",       Map.of("en", "Saudi Arabia",        "de", "Saudi-Arabien",                  "fr", "Arabie saoudite"));
        m.put("Singapore",          Map.of("en", "Singapore",           "de", "Singapur",                       "fr", "Singapour"));
        m.put("Sri Lanka",          Map.of("en", "Sri Lanka",           "de", "Sri Lanka",                      "fr", "Sri Lanka"));
        m.put("Syria",              Map.of("en", "Syria",               "de", "Syrien",                         "fr", "Syrie"));
        m.put("Taiwan",             Map.of("en", "Taiwan",              "de", "Taiwan",                         "fr", "Taïwan"));
        m.put("Tajikistan",         Map.of("en", "Tajikistan",          "de", "Tadschikistan",                  "fr", "Tadjikistan"));
        m.put("Thailand",           Map.of("en", "Thailand",            "de", "Thailand",                       "fr", "Thaïlande"));
        m.put("Timor-Leste",        Map.of("en", "Timor-Leste",         "de", "Osttimor",                       "fr", "Timor oriental"));
        m.put("Turkmenistan",       Map.of("en", "Turkmenistan",        "de", "Turkmenistan",                   "fr", "Turkménistan"));
        m.put("United Arab Emirates", Map.of("en", "United Arab Emirates", "de", "Vereinigte Arabische Emirate","fr", "Émirats arabes unis"));
        m.put("Uzbekistan",         Map.of("en", "Uzbekistan",          "de", "Usbekistan",                     "fr", "Ouzbékistan"));
        m.put("Vietnam",            Map.of("en", "Vietnam",             "de", "Vietnam",                        "fr", "Viêt Nam"));
        m.put("Yemen",              Map.of("en", "Yemen",               "de", "Jemen",                          "fr", "Yémen"));
        // Africa
        m.put("Algeria",            Map.of("en", "Algeria",             "de", "Algerien",                       "fr", "Algérie"));
        m.put("Angola",             Map.of("en", "Angola",              "de", "Angola",                         "fr", "Angola"));
        m.put("Benin",              Map.of("en", "Benin",               "de", "Benin",                          "fr", "Bénin"));
        m.put("Botswana",           Map.of("en", "Botswana",            "de", "Botswana",                       "fr", "Botswana"));
        m.put("Burkina Faso",       Map.of("en", "Burkina Faso",        "de", "Burkina Faso",                   "fr", "Burkina Faso"));
        m.put("Burundi",            Map.of("en", "Burundi",             "de", "Burundi",                        "fr", "Burundi"));
        m.put("Cameroon",           Map.of("en", "Cameroon",            "de", "Kamerun",                        "fr", "Cameroun"));
        m.put("Cape Verde",         Map.of("en", "Cape Verde",          "de", "Kap Verde",                      "fr", "Cap-Vert"));
        m.put("Central African Republic", Map.of("en", "Central African Republic", "de", "Zentralafrikanische Republik", "fr", "République centrafricaine"));
        m.put("Chad",               Map.of("en", "Chad",                "de", "Tschad",                         "fr", "Tchad"));
        m.put("Comoros",            Map.of("en", "Comoros",             "de", "Komoren",                        "fr", "Comores"));
        m.put("Republic of the Congo", Map.of("en", "Republic of the Congo", "de", "Republik Kongo",           "fr", "République du Congo"));
        m.put("DR Congo",           Map.of("en", "DR Congo",            "de", "Demokratische Republik Kongo",  "fr", "République démocratique du Congo"));
        m.put("Côte d'Ivoire (Ivory Coast)", Map.of("en", "Côte d'Ivoire (Ivory Coast)", "de", "Elfenbeinküste", "fr", "Côte d'Ivoire"));
        m.put("Djibouti",           Map.of("en", "Djibouti",            "de", "Dschibuti",                      "fr", "Djibouti"));
        m.put("Egypt",              Map.of("en", "Egypt",               "de", "Ägypten",                        "fr", "Égypte"));
        m.put("Equatorial Guinea",  Map.of("en", "Equatorial Guinea",   "de", "Äquatorialguinea",               "fr", "Guinée équatoriale"));
        m.put("Eritrea",            Map.of("en", "Eritrea",             "de", "Eritrea",                        "fr", "Érythrée"));
        m.put("Eswatini (Swaziland)", Map.of("en", "Eswatini (Swaziland)", "de", "Eswatini (Swasiland)",       "fr", "Eswatini (Swaziland)"));
        m.put("Ethiopia",           Map.of("en", "Ethiopia",            "de", "Äthiopien",                      "fr", "Éthiopie"));
        m.put("Gabon",              Map.of("en", "Gabon",               "de", "Gabun",                          "fr", "Gabon"));
        m.put("Gambia",             Map.of("en", "Gambia",              "de", "Gambia",                         "fr", "Gambie"));
        m.put("Ghana",              Map.of("en", "Ghana",               "de", "Ghana",                          "fr", "Ghana"));
        m.put("Guinea",             Map.of("en", "Guinea",              "de", "Guinea",                         "fr", "Guinée"));
        m.put("Guinea-Bissau",      Map.of("en", "Guinea-Bissau",       "de", "Guinea-Bissau",                  "fr", "Guinée-Bissau"));
        m.put("Kenya",              Map.of("en", "Kenya",               "de", "Kenia",                          "fr", "Kenya"));
        m.put("Lesotho",            Map.of("en", "Lesotho",             "de", "Lesotho",                        "fr", "Lesotho"));
        m.put("Liberia",            Map.of("en", "Liberia",             "de", "Liberia",                        "fr", "Libéria"));
        m.put("Libya",              Map.of("en", "Libya",               "de", "Libyen",                         "fr", "Libye"));
        m.put("Madagascar",         Map.of("en", "Madagascar",          "de", "Madagaskar",                     "fr", "Madagascar"));
        m.put("Malawi",             Map.of("en", "Malawi",              "de", "Malawi",                         "fr", "Malawi"));
        m.put("Mali",               Map.of("en", "Mali",                "de", "Mali",                           "fr", "Mali"));
        m.put("Mauritania",         Map.of("en", "Mauritania",          "de", "Mauretanien",                    "fr", "Mauritanie"));
        m.put("Mauritius",          Map.of("en", "Mauritius",           "de", "Mauritius",                      "fr", "Maurice"));
        m.put("Morocco",            Map.of("en", "Morocco",             "de", "Marokko",                        "fr", "Maroc"));
        m.put("Mozambique",         Map.of("en", "Mozambique",          "de", "Mosambik",                       "fr", "Mozambique"));
        m.put("Namibia",            Map.of("en", "Namibia",             "de", "Namibia",                        "fr", "Namibie"));
        m.put("Niger",              Map.of("en", "Niger",               "de", "Niger",                          "fr", "Niger"));
        m.put("Nigeria",            Map.of("en", "Nigeria",             "de", "Nigeria",                        "fr", "Nigéria"));
        m.put("Rwanda",             Map.of("en", "Rwanda",              "de", "Ruanda",                         "fr", "Rwanda"));
        m.put("São Tomé and Príncipe", Map.of("en", "São Tomé and Príncipe", "de", "São Tomé und Príncipe",     "fr", "Sao Tomé-et-Principe"));
        m.put("Senegal",            Map.of("en", "Senegal",             "de", "Senegal",                        "fr", "Sénégal"));
        m.put("Seychelles",         Map.of("en", "Seychelles",          "de", "Seychellen",                     "fr", "Seychelles"));
        m.put("Sierra Leone",       Map.of("en", "Sierra Leone",        "de", "Sierra Leone",                   "fr", "Sierra Leone"));
        m.put("Somalia",            Map.of("en", "Somalia",             "de", "Somalia",                        "fr", "Somalie"));
        m.put("South Africa",       Map.of("en", "South Africa",        "de", "Südafrika",                      "fr", "Afrique du Sud"));
        m.put("South Sudan",        Map.of("en", "South Sudan",         "de", "Südsudan",                       "fr", "Soudan du Sud"));
        m.put("Sudan",              Map.of("en", "Sudan",               "de", "Sudan",                          "fr", "Soudan"));
        m.put("Tanzania",           Map.of("en", "Tanzania",            "de", "Tansania",                       "fr", "Tanzanie"));
        m.put("Togo",               Map.of("en", "Togo",                "de", "Togo",                           "fr", "Togo"));
        m.put("Tunisia",            Map.of("en", "Tunisia",             "de", "Tunesien",                       "fr", "Tunisie"));
        m.put("Uganda",             Map.of("en", "Uganda",              "de", "Uganda",                         "fr", "Ouganda"));
        m.put("Zambia",             Map.of("en", "Zambia",              "de", "Sambia",                         "fr", "Zambie"));
        m.put("Zimbabwe",           Map.of("en", "Zimbabwe",            "de", "Simbabwe",                       "fr", "Zimbabwe"));
        // Americas
        m.put("Antigua and Barbuda", Map.of("en", "Antigua and Barbuda", "de", "Antigua und Barbuda",           "fr", "Antigua-et-Barbuda"));
        m.put("Argentina",          Map.of("en", "Argentina",           "de", "Argentinien",                    "fr", "Argentine"));
        m.put("Aruba",              Map.of("en", "Aruba",               "de", "Aruba",                          "fr", "Aruba"));
        m.put("Bahamas",            Map.of("en", "Bahamas",             "de", "Bahamas",                        "fr", "Bahamas"));
        m.put("Barbados",           Map.of("en", "Barbados",            "de", "Barbados",                       "fr", "Barbade"));
        m.put("Belize",             Map.of("en", "Belize",              "de", "Belize",                         "fr", "Belize"));
        m.put("Bermuda",            Map.of("en", "Bermuda",             "de", "Bermuda",                        "fr", "Bermudes"));
        m.put("Bolivia",            Map.of("en", "Bolivia",             "de", "Bolivien",                       "fr", "Bolivie"));
        m.put("Brazil",             Map.of("en", "Brazil",              "de", "Brasilien",                      "fr", "Brésil"));
        m.put("Cayman Islands",     Map.of("en", "Cayman Islands",      "de", "Kaimaninseln",                   "fr", "Îles Caïmans"));
        m.put("Chile",              Map.of("en", "Chile",               "de", "Chile",                          "fr", "Chili"));
        m.put("Colombia",           Map.of("en", "Colombia",            "de", "Kolumbien",                      "fr", "Colombie"));
        m.put("Costa Rica",         Map.of("en", "Costa Rica",          "de", "Costa Rica",                     "fr", "Costa Rica"));
        m.put("Cuba",               Map.of("en", "Cuba",                "de", "Kuba",                           "fr", "Cuba"));
        m.put("Dominica",           Map.of("en", "Dominica",            "de", "Dominica",                       "fr", "Dominique"));
        m.put("Dominican Republic", Map.of("en", "Dominican Republic",  "de", "Dominikanische Republik",        "fr", "République dominicaine"));
        m.put("Ecuador",            Map.of("en", "Ecuador",             "de", "Ecuador",                        "fr", "Équateur"));
        m.put("El Salvador",        Map.of("en", "El Salvador",         "de", "El Salvador",                    "fr", "Salvador"));
        m.put("Grenada",            Map.of("en", "Grenada",             "de", "Grenada",                        "fr", "Grenade"));
        m.put("Guatemala",          Map.of("en", "Guatemala",           "de", "Guatemala",                      "fr", "Guatemala"));
        m.put("Guyana",             Map.of("en", "Guyana",              "de", "Guyana",                         "fr", "Guyana"));
        m.put("Haiti",              Map.of("en", "Haiti",               "de", "Haiti",                          "fr", "Haïti"));
        m.put("Honduras",           Map.of("en", "Honduras",            "de", "Honduras",                       "fr", "Honduras"));
        m.put("Jamaica",            Map.of("en", "Jamaica",             "de", "Jamaika",                        "fr", "Jamaïque"));
        m.put("Mexico",             Map.of("en", "Mexico",              "de", "Mexiko",                         "fr", "Mexique"));
        m.put("Nicaragua",          Map.of("en", "Nicaragua",           "de", "Nicaragua",                      "fr", "Nicaragua"));
        m.put("Panama",             Map.of("en", "Panama",              "de", "Panama",                         "fr", "Panama"));
        m.put("Paraguay",           Map.of("en", "Paraguay",            "de", "Paraguay",                       "fr", "Paraguay"));
        m.put("Peru",               Map.of("en", "Peru",                "de", "Peru",                           "fr", "Pérou"));
        m.put("Puerto Rico",        Map.of("en", "Puerto Rico",         "de", "Puerto Rico",                    "fr", "Porto Rico"));
        m.put("Saint Kitts and Nevis", Map.of("en", "Saint Kitts and Nevis", "de", "St. Kitts und Nevis",      "fr", "Saint-Kitts-et-Nevis"));
        m.put("Saint Lucia",        Map.of("en", "Saint Lucia",         "de", "St. Lucia",                      "fr", "Sainte-Lucie"));
        m.put("Saint Vincent and the Grenadines", Map.of("en", "Saint Vincent and the Grenadines", "de", "St. Vincent und die Grenadinen", "fr", "Saint-Vincent-et-les-Grenadines"));
        m.put("Suriname",           Map.of("en", "Suriname",            "de", "Suriname",                       "fr", "Suriname"));
        m.put("Trinidad and Tobago", Map.of("en", "Trinidad and Tobago", "de", "Trinidad und Tobago",           "fr", "Trinité-et-Tobago"));
        m.put("Uruguay",            Map.of("en", "Uruguay",             "de", "Uruguay",                        "fr", "Uruguay"));
        m.put("Venezuela",          Map.of("en", "Venezuela",           "de", "Venezuela",                      "fr", "Venezuela"));
        m.put("British Virgin Islands", Map.of("en", "British Virgin Islands", "de", "Britische Jungferninseln","fr", "Îles Vierges britanniques"));
        m.put("United States Virgin Islands", Map.of("en", "United States Virgin Islands", "de", "Amerikanische Jungferninseln", "fr", "Îles Vierges américaines"));
        // Oceania
        m.put("American Samoa",     Map.of("en", "American Samoa",      "de", "Amerikanisch-Samoa",             "fr", "Samoa américaines"));
        m.put("Cook Islands",       Map.of("en", "Cook Islands",        "de", "Cookinseln",                     "fr", "Îles Cook"));
        m.put("Fiji",               Map.of("en", "Fiji",                "de", "Fidschi",                        "fr", "Fidji"));
        m.put("French Polynesia",   Map.of("en", "French Polynesia",    "de", "Französisch-Polynesien",         "fr", "Polynésie française"));
        m.put("Guam",               Map.of("en", "Guam",                "de", "Guam",                           "fr", "Guam"));
        m.put("Kiribati",           Map.of("en", "Kiribati",            "de", "Kiribati",                       "fr", "Kiribati"));
        m.put("Marshall Islands",   Map.of("en", "Marshall Islands",    "de", "Marshallinseln",                 "fr", "Îles Marshall"));
        m.put("Micronesia",         Map.of("en", "Micronesia",          "de", "Mikronesien",                    "fr", "Micronésie"));
        m.put("Nauru",              Map.of("en", "Nauru",               "de", "Nauru",                          "fr", "Nauru"));
        m.put("New Zealand",        Map.of("en", "New Zealand",         "de", "Neuseeland",                     "fr", "Nouvelle-Zélande"));
        m.put("Palau",              Map.of("en", "Palau",               "de", "Palau",                          "fr", "Palaos"));
        m.put("Papua New Guinea",   Map.of("en", "Papua New Guinea",    "de", "Papua-Neuguinea",                "fr", "Papouasie-Nouvelle-Guinée"));
        m.put("Samoa",              Map.of("en", "Samoa",               "de", "Samoa",                          "fr", "Samoa"));
        m.put("Solomon Islands",    Map.of("en", "Solomon Islands",     "de", "Salomonen",                      "fr", "Îles Salomon"));
        m.put("Tonga",              Map.of("en", "Tonga",               "de", "Tonga",                          "fr", "Tonga"));
        m.put("Tuvalu",             Map.of("en", "Tuvalu",              "de", "Tuvalu",                         "fr", "Tuvalu"));
        m.put("Vanuatu",            Map.of("en", "Vanuatu",             "de", "Vanuatu",                        "fr", "Vanuatu"));
        COUNTRY_TRANSLATIONS = m;
    }

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

    private String translate(Map<String, Map<String, String>> translations, String key, String lang) {
        if (key == null) return null;
        Map<String, String> langMap = translations.get(key);
        if (langMap == null) return key; // fallback: return original
        return langMap.getOrDefault(lang, key);
    }
}


