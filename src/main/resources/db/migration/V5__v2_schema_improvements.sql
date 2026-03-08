-- =============================================================
-- V5: Schema improvements for V2 API
--
-- Changes:
--   1. Add multilingual name columns to `sports` (name_de, name_en, name_fr)
--      and a `score_type` column (moved from results to sports level,
--      since every sport has exactly one competition format).
--   2. Add multilingual name columns to `countries` (name_de, name_en, name_fr).
--   3. Add UNIQUE constraint on results(event_id, athlete_id) to enforce
--      one result per athlete per sport.
--   4. Migrate existing translation data from application-level maps into DB.
-- =============================================================

-- ---------------------------------------------------------------
-- 1. sports: add multilingual names + score_type
-- ---------------------------------------------------------------
ALTER TABLE sports
    ADD COLUMN name_en  VARCHAR(150) NULL AFTER name,
    ADD COLUMN name_de  VARCHAR(150) NULL AFTER name_en,
    ADD COLUMN name_fr  VARCHAR(150) NULL AFTER name_de,
    ADD COLUMN score_type ENUM('PTS','WINS','TIME') NULL AFTER name_fr;

-- Populate multilingual sport names from existing data
UPDATE sports SET name_en = name;

UPDATE sports SET
    name_de = CASE name
        WHEN 'Alpine Skiing'              THEN 'Ski Alpin'
        WHEN 'Biathlon'                   THEN 'Biathlon'
        WHEN 'Cross-Country Skiing'       THEN 'Skilanglauf'
        WHEN 'Ski Jumping'                THEN 'Skispringen'
        WHEN 'Nordic Combined'            THEN 'Nordische Kombination'
        WHEN 'Freestyle Skiing'           THEN 'Freestyle-Skiing'
        WHEN 'Snowboarding'               THEN 'Snowboarden'
        WHEN 'Figure Skating'             THEN 'Eiskunstlauf'
        WHEN 'Speed Skating'              THEN 'Eisschnelllauf'
        WHEN 'Short Track Speed Skating'  THEN 'Shorttrack'
        WHEN 'Ice Hockey'                 THEN 'Eishockey'
        WHEN 'Curling'                    THEN 'Curling'
        WHEN 'Bobsleigh'                  THEN 'Bobfahren'
        WHEN 'Skeleton'                   THEN 'Skeleton'
        WHEN 'Luge'                       THEN 'Rodeln'
        ELSE name
    END,
    name_fr = CASE name
        WHEN 'Alpine Skiing'              THEN 'Ski alpin'
        WHEN 'Biathlon'                   THEN 'Biathlon'
        WHEN 'Cross-Country Skiing'       THEN 'Ski de fond'
        WHEN 'Ski Jumping'                THEN 'Saut à ski'
        WHEN 'Nordic Combined'            THEN 'Combiné nordique'
        WHEN 'Freestyle Skiing'           THEN 'Ski acrobatique'
        WHEN 'Snowboarding'               THEN 'Snowboard'
        WHEN 'Figure Skating'             THEN 'Patinage artistique'
        WHEN 'Speed Skating'              THEN 'Patinage de vitesse'
        WHEN 'Short Track Speed Skating'  THEN 'Patinage de vitesse sur piste courte'
        WHEN 'Ice Hockey'                 THEN 'Hockey sur glace'
        WHEN 'Curling'                    THEN 'Curling'
        WHEN 'Bobsleigh'                  THEN 'Bobsleigh'
        WHEN 'Skeleton'                   THEN 'Skeleton'
        WHEN 'Luge'                       THEN 'Luge'
        ELSE name
    END;

-- Migrate score_type from results to sports (take the most common value per sport)
UPDATE sports s
    JOIN (
        SELECT event_id, score_type,
               COUNT(*) AS cnt
        FROM results
        WHERE score_type IS NOT NULL AND event_id IS NOT NULL
        GROUP BY event_id, score_type
        ORDER BY cnt DESC
    ) r ON r.event_id = s.id
SET s.score_type = r.score_type
WHERE s.score_type IS NULL;

-- Default fallback: TIME for any sport still missing a score_type
UPDATE sports SET score_type = 'TIME' WHERE score_type IS NULL;

-- ---------------------------------------------------------------
-- 2. countries: add multilingual name columns
-- ---------------------------------------------------------------
ALTER TABLE countries
    ADD COLUMN name_en VARCHAR(150) NULL AFTER name,
    ADD COLUMN name_de VARCHAR(150) NULL AFTER name_en,
    ADD COLUMN name_fr VARCHAR(150) NULL AFTER name_de;

-- Populate English names (same as existing `name`)
UPDATE countries SET name_en = name;

-- Populate German and French names for known countries
UPDATE countries SET
    name_de = CASE name
        WHEN 'United States'           THEN 'Vereinigte Staaten'
        WHEN 'Germany'                 THEN 'Deutschland'
        WHEN 'France'                  THEN 'Frankreich'
        WHEN 'Norway'                  THEN 'Norwegen'
        WHEN 'Sweden'                  THEN 'Schweden'
        WHEN 'Canada'                  THEN 'Kanada'
        WHEN 'Austria'                 THEN 'Österreich'
        WHEN 'Switzerland'             THEN 'Schweiz'
        WHEN 'Italy'                   THEN 'Italien'
        WHEN 'Japan'                   THEN 'Japan'
        WHEN 'China'                   THEN 'China'
        WHEN 'South Korea'             THEN 'Südkorea'
        WHEN 'Russia'                  THEN 'Russland'
        WHEN 'Finland'                 THEN 'Finnland'
        WHEN 'Netherlands'             THEN 'Niederlande'
        WHEN 'Czech Republic'          THEN 'Tschechien'
        WHEN 'Poland'                  THEN 'Polen'
        WHEN 'Great Britain'           THEN 'Großbritannien'
        WHEN 'Spain'                   THEN 'Spanien'
        WHEN 'Australia'               THEN 'Australien'
        WHEN 'Albania'                 THEN 'Albanien'
        WHEN 'Andorra'                 THEN 'Andorra'
        WHEN 'Armenia'                 THEN 'Armenien'
        WHEN 'Azerbaijan'              THEN 'Aserbaidschan'
        WHEN 'Belarus'                 THEN 'Weißrussland'
        WHEN 'Belgium'                 THEN 'Belgien'
        WHEN 'Bosnia and Herzegovina'  THEN 'Bosnien und Herzegowina'
        WHEN 'Bulgaria'                THEN 'Bulgarien'
        WHEN 'Croatia'                 THEN 'Kroatien'
        WHEN 'Cyprus'                  THEN 'Zypern'
        WHEN 'Denmark'                 THEN 'Dänemark'
        WHEN 'Estonia'                 THEN 'Estland'
        WHEN 'Georgia'                 THEN 'Georgien'
        WHEN 'Greece'                  THEN 'Griechenland'
        WHEN 'Hungary'                 THEN 'Ungarn'
        WHEN 'Iceland'                 THEN 'Island'
        WHEN 'Ireland'                 THEN 'Irland'
        WHEN 'Israel'                  THEN 'Israel'
        WHEN 'Kazakhstan'              THEN 'Kasachstan'
        WHEN 'Kosovo'                  THEN 'Kosovo'
        WHEN 'Latvia'                  THEN 'Lettland'
        WHEN 'Liechtenstein'           THEN 'Liechtenstein'
        WHEN 'Lithuania'               THEN 'Litauen'
        WHEN 'Luxembourg'              THEN 'Luxemburg'
        WHEN 'Malta'                   THEN 'Malta'
        WHEN 'Moldova'                 THEN 'Moldau'
        WHEN 'Monaco'                  THEN 'Monaco'
        WHEN 'Montenegro'              THEN 'Montenegro'
        WHEN 'North Macedonia'         THEN 'Nordmazedonien'
        WHEN 'Portugal'                THEN 'Portugal'
        WHEN 'Romania'                 THEN 'Rumänien'
        WHEN 'San Marino'              THEN 'San Marino'
        WHEN 'Serbia'                  THEN 'Serbien'
        WHEN 'Slovakia'                THEN 'Slowakei'
        WHEN 'Slovenia'                THEN 'Slowenien'
        WHEN 'Turkey'                  THEN 'Türkei'
        WHEN 'Ukraine'                 THEN 'Ukraine'
        ELSE name
    END,
    name_fr = CASE name
        WHEN 'United States'           THEN 'États-Unis'
        WHEN 'Germany'                 THEN 'Allemagne'
        WHEN 'France'                  THEN 'France'
        WHEN 'Norway'                  THEN 'Norvège'
        WHEN 'Sweden'                  THEN 'Suède'
        WHEN 'Canada'                  THEN 'Canada'
        WHEN 'Austria'                 THEN 'Autriche'
        WHEN 'Switzerland'             THEN 'Suisse'
        WHEN 'Italy'                   THEN 'Italie'
        WHEN 'Japan'                   THEN 'Japon'
        WHEN 'China'                   THEN 'Chine'
        WHEN 'South Korea'             THEN 'Corée du Sud'
        WHEN 'Russia'                  THEN 'Russie'
        WHEN 'Finland'                 THEN 'Finlande'
        WHEN 'Netherlands'             THEN 'Pays-Bas'
        WHEN 'Czech Republic'          THEN 'République tchèque'
        WHEN 'Poland'                  THEN 'Pologne'
        WHEN 'Great Britain'           THEN 'Grande-Bretagne'
        WHEN 'Spain'                   THEN 'Espagne'
        WHEN 'Australia'               THEN 'Australie'
        WHEN 'Albania'                 THEN 'Albanie'
        WHEN 'Andorra'                 THEN 'Andorre'
        WHEN 'Armenia'                 THEN 'Arménie'
        WHEN 'Azerbaijan'              THEN 'Azerbaïdjan'
        WHEN 'Belarus'                 THEN 'Biélorussie'
        WHEN 'Belgium'                 THEN 'Belgique'
        WHEN 'Bosnia and Herzegovina'  THEN 'Bosnie-Herzégovine'
        WHEN 'Bulgaria'                THEN 'Bulgarie'
        WHEN 'Croatia'                 THEN 'Croatie'
        WHEN 'Cyprus'                  THEN 'Chypre'
        WHEN 'Denmark'                 THEN 'Danemark'
        WHEN 'Estonia'                 THEN 'Estonie'
        WHEN 'Georgia'                 THEN 'Géorgie'
        WHEN 'Greece'                  THEN 'Grèce'
        WHEN 'Hungary'                 THEN 'Hongrie'
        WHEN 'Iceland'                 THEN 'Islande'
        WHEN 'Ireland'                 THEN 'Irlande'
        WHEN 'Israel'                  THEN 'Israël'
        WHEN 'Kazakhstan'              THEN 'Kazakhstan'
        WHEN 'Kosovo'                  THEN 'Kosovo'
        WHEN 'Latvia'                  THEN 'Lettonie'
        WHEN 'Liechtenstein'           THEN 'Liechtenstein'
        WHEN 'Lithuania'               THEN 'Lituanie'
        WHEN 'Luxembourg'              THEN 'Luxembourg'
        WHEN 'Malta'                   THEN 'Malte'
        WHEN 'Moldova'                 THEN 'Moldavie'
        WHEN 'Monaco'                  THEN 'Monaco'
        WHEN 'Montenegro'              THEN 'Monténégro'
        WHEN 'North Macedonia'         THEN 'Macédoine du Nord'
        WHEN 'Portugal'                THEN 'Portugal'
        WHEN 'Romania'                 THEN 'Roumanie'
        WHEN 'San Marino'              THEN 'Saint-Marin'
        WHEN 'Serbia'                  THEN 'Serbie'
        WHEN 'Slovakia'                THEN 'Slovaquie'
        WHEN 'Slovenia'                THEN 'Slovénie'
        WHEN 'Turkey'                  THEN 'Turquie'
        WHEN 'Ukraine'                 THEN 'Ukraine'
        ELSE name
    END;

-- Fallback: if name_de/name_fr still NULL, use the English name
UPDATE countries SET name_de = name_en WHERE name_de IS NULL;
UPDATE countries SET name_fr = name_en WHERE name_fr IS NULL;

-- ---------------------------------------------------------------
-- 3. results: enforce one result per athlete per sport
-- ---------------------------------------------------------------

-- Remove duplicate results, keeping the one with the lowest id per (event_id, athlete_id)
DELETE r1 FROM results r1
    INNER JOIN results r2
    ON r1.event_id = r2.event_id
   AND r1.athlete_id = r2.athlete_id
   AND r1.id > r2.id;

-- Now add the unique constraint
ALTER TABLE results
    ADD CONSTRAINT uq_results_event_athlete UNIQUE (event_id, athlete_id);

