package com.example.phchat.model

data class Province(
    val code: String,
    val name: String,
    val capitalOrCenter: String,
    val culturalGlyph: String = "📍"
)

data class PhilippineRegion(
    val code: String,
    val name: String,
    val islandGroup: String, // Luzon, Visayas, Mindanao
    val culturalGlyph: String,
    val provinces: List<Province>
)

object PhilippineAdministrativeDivisions {
    val regions: List<PhilippineRegion> = listOf(
        PhilippineRegion(
            code = "NCR",
            name = "National Capital Region (Metro Manila)",
            islandGroup = "Luzon",
            culturalGlyph = "🏙️",
            provinces = listOf(
                Province("NCR-MNL", "City of Manila", "Manila", "🏛️"),
                Province("NCR-QC", "Quezon City", "QC", "🌳"),
                Province("NCR-MAK", "Makati City", "Makati", "🏢"),
                Province("NCR-TAG", "Taguig (BGC)", "Taguig", "✨"),
                Province("NCR-PAS", "Pasig City", "Pasig", "🌆"),
                Province("NCR-CAL", "Caloocan City", "Caloocan", "🚂"),
                Province("NCR-ALL", "Metro Manila General", "Metro Manila", "🏙️")
            )
        ),
        PhilippineRegion(
            code = "CAR",
            name = "Cordillera Administrative Region",
            islandGroup = "Luzon",
            culturalGlyph = "🌲",
            provinces = listOf(
                Province("BEN", "Benguet (Baguio City)", "La Trinidad", "🍓"),
                Province("ABR", "Abra", "Bangued", "⛰️"),
                Province("APA", "Apayao", "Kabugao", "🌿"),
                Province("IFU", "Ifugao (Rice Terraces)", "Lagawe", "🌾"),
                Province("KAL", "Kalinga", "Tabuk", "🦅"),
                Province("MOU", "Mountain Province (Sagada)", "Bontoc", "☕")
            )
        ),
        PhilippineRegion(
            code = "Region I",
            name = "Ilocos Region",
            islandGroup = "Luzon",
            culturalGlyph = "🏰",
            provinces = listOf(
                Province("ILN", "Ilocos Norte", "Laoag", "💨"),
                Province("ILS", "Ilocos Sur (Vigan)", "Vigan", "🧱"),
                Province("LUN", "La Union (Elyu Surfing)", "San Fernando", "🏄"),
                Province("PAN", "Pangasinan", "Lingayen", "🐟")
            )
        ),
        PhilippineRegion(
            code = "Region II",
            name = "Cagayan Valley",
            islandGroup = "Luzon",
            culturalGlyph = "🌾",
            provinces = listOf(
                Province("BAT", "Batanes", "Basco", "🪨"),
                Province("CAG", "Cagayan", "Tuguegarao", "🌊"),
                Province("ISA", "Isabela", "Ilagan", "🌽"),
                Province("NUV", "Nueva Vizcaya", "Bayombong", "🍊"),
                Province("QUI", "Quirino", "Cabarroguis", "🛶")
            )
        ),
        PhilippineRegion(
            code = "Region III",
            name = "Central Luzon",
            islandGroup = "Luzon",
            culturalGlyph = "🍲",
            provinces = listOf(
                Province("PAM", "Pampanga (Culinary Capital)", "San Fernando", "🍲"),
                Province("BUL", "Bulacan", "Malolos", "🎆"),
                Province("BAT", "Bataan", "Balanga", "⚓"),
                Province("NUE", "Nueva Ecija", "Palayan", "🌾"),
                Province("TAR", "Tarlac", "Tarlac City", "🍬"),
                Province("ZAM", "Zambales (Subic)", "Iba", "🥭"),
                Province("AUR", "Aurora (Baler)", "Baler", "🌊")
            )
        ),
        PhilippineRegion(
            code = "Region IV-A",
            name = "CALABARZON",
            islandGroup = "Luzon",
            culturalGlyph = "🌋",
            provinces = listOf(
                Province("BTG", "Batangas (Taal Volcano)", "Batangas City", "☕"),
                Province("CAV", "Cavite (Tagaytay)", "Imus", "🏰"),
                Province("LAG", "Laguna (Hot Springs)", "Santa Cruz", "♨️"),
                Province("RIZ", "Rizal (Antipolo)", "Antipolo", "🎨"),
                Province("QUE", "Quezon Province", "Lucena", "🥥")
            )
        ),
        PhilippineRegion(
            code = "Region IV-B",
            name = "MIMAROPA",
            islandGroup = "Luzon",
            culturalGlyph = "🏝️",
            provinces = listOf(
                Province("PLW", "Palawan (El Nido & Coron)", "Puerto Princesa", "🏝️"),
                Province("OCM", "Occidental Mindoro", "Mamburao", "🐃"),
                Province("ORM", "Oriental Mindoro (Puerto Galera)", "Calapan", "⛵"),
                Province("MAR", "Marinduque", "Boac", "🎭"),
                Province("ROM", "Romblon (Marble Capital)", "Romblon", "🏛️")
            )
        ),
        PhilippineRegion(
            code = "Region V",
            name = "Bicol Region",
            islandGroup = "Luzon",
            culturalGlyph = "🌶️",
            provinces = listOf(
                Province("ALB", "Albay (Mayon Volcano)", "Legazpi", "🌋"),
                Province("CAS", "Camarines Sur (Naga City)", "Pili", "⛵"),
                Province("CAN", "Camarines Norte", "Daet", "🍍"),
                Province("CAT", "Catanduanes", "Virac", "🏄"),
                Province("MAS", "Masbate", "Masbate City", "🤠"),
                Province("SOR", "Sorsogon (Donsol Butanding)", "Sorsogon City", "🐋")
            )
        ),
        PhilippineRegion(
            code = "Region VI",
            name = "Western Visayas",
            islandGroup = "Visayas",
            culturalGlyph = "⛵",
            provinces = listOf(
                Province("ILO", "Iloilo (Dinagyang)", "Iloilo City", "⛵"),
                Province("AKL", "Aklan (Boracay Island)", "Kalibo", "🏖️"),
                Province("ANT", "Antique", "San Jose de Buenavista", "⛰️"),
                Province("CAP", "Capiz (Seafood Capital)", "Roxas City", "🦀"),
                Province("GUI", "Guimaras (Mango Capital)", "Jordan", "🥭"),
                Province("NEG", "Negros Occidental (Bacolod)", "Bacolod", "🎭")
            )
        ),
        PhilippineRegion(
            code = "Region VII",
            name = "Central Visayas",
            islandGroup = "Visayas",
            culturalGlyph = "🏝️",
            provinces = listOf(
                Province("CEB", "Cebu (Sugbo / Queen City)", "Cebu City", "🏝️"),
                Province("BOH", "Bohol (Chocolate Hills)", "Tagbilaran", "🐒"),
                Province("NER", "Negros Oriental (Dumaguete)", "Dumaguete", "🐬"),
                Province("SIQ", "Siquijor (Mystic Island)", "Siquijor", "🔮")
            )
        ),
        PhilippineRegion(
            code = "Region VIII",
            name = "Eastern Visayas",
            islandGroup = "Visayas",
            culturalGlyph = "🌉",
            provinces = listOf(
                Province("LEY", "Leyte (Tacloban)", "Tacloban", "🌉"),
                Province("BIL", "Biliran", "Naval", "💦"),
                Province("EAS", "Eastern Samar", "Borongan", "🏄"),
                Province("NOS", "Northern Samar", "Catarman", "🌊"),
                Province("SAM", "Samar (Catbalogan)", "Catbalogan", "🛶"),
                Province("SOL", "Southern Leyte", "Maasin", "🐋")
            )
        ),
        PhilippineRegion(
            code = "Region IX",
            name = "Zamboanga Peninsula",
            islandGroup = "Mindanao",
            culturalGlyph = "⛵",
            provinces = listOf(
                Province("ZAN", "Zamboanga del Norte (Dapitan)", "Dipolog", "🏖️"),
                Province("ZAS", "Zamboanga del Sur (Zamboanga City)", "Pagadian", "🌺"),
                Province("ZSI", "Zamboanga Sibugay", "Ipil", "🐟")
            )
        ),
        PhilippineRegion(
            code = "Region X",
            name = "Northern Mindanao",
            islandGroup = "Mindanao",
            culturalGlyph = "🍍",
            provinces = listOf(
                Province("MOR", "Misamis Oriental (Cagayan de Oro)", "Cagayan de Oro", "🛶"),
                Province("BUK", "Bukidnon (Dahilayan)", "Malaybalay", "🍍"),
                Province("CAM", "Camiguin (Island Born of Fire)", "Mambajao", "🌋"),
                Province("LAN", "Lanao del Norte (Iligan)", "Tubod", "💦"),
                Province("MOC", "Misamis Occidental", "Oroquieta", "🐟")
            )
        ),
        PhilippineRegion(
            code = "Region XI",
            name = "Davao Region",
            islandGroup = "Mindanao",
            culturalGlyph = "🦅",
            provinces = listOf(
                Province("DVO", "Davao del Sur (Davao City)", "Davao City", "🦅"),
                Province("DVN", "Davao del Norte (Samal Island)", "Tagum", "🏝️"),
                Province("DOR", "Davao Oriental (Mati Pujada)", "Mati", "🌊"),
                Province("DOR2", "Davao de Oro (Compostela)", "Nabunturan", "✨"),
                Province("DOC", "Davao Occidental", "Malita", "⛰️")
            )
        ),
        PhilippineRegion(
            code = "Region XII",
            name = "SOCCSKSARGEN",
            islandGroup = "Mindanao",
            culturalGlyph = "🐟",
            provinces = listOf(
                Province("SCO", "South Cotabato (GenSan / Tuna Capital)", "Koronadal", "🐟"),
                Province("COT", "Cotabato (North Cotabato)", "Kidapawan", "⛰️"),
                Province("SAR", "Sarangani", "Alabel", "🏄"),
                Province("SKU", "Sultan Kudarat", "Isulan", "🌾")
            )
        ),
        PhilippineRegion(
            code = "Region XIII",
            name = "Caraga Region",
            islandGroup = "Mindanao",
            culturalGlyph = "🏄",
            provinces = listOf(
                Province("SUN", "Surigao del Norte (Siargao Island)", "Surigao City", "🏄"),
                Province("SUS", "Surigao del Sur (Enchanted River)", "Tandag", "💦"),
                Province("ADN", "Agusan del Norte (Butuan City)", "Cabadbaran", "⛵"),
                Province("ADS", "Agusan del Sur (Agusan Marsh)", "Prosperidad", "🐊"),
                Province("DIN", "Dinagat Islands", "San Jose", "🏝️")
            )
        ),
        PhilippineRegion(
            code = "BARMM",
            name = "Bangsamoro Autonomous Region (BARMM)",
            islandGroup = "Mindanao",
            culturalGlyph = "🕌",
            provinces = listOf(
                Province("MAG", "Maguindanao del Norte & Sur", "Buluan", "🕌"),
                Province("LAS", "Lanao del Sur (Marawi)", "Marawi", "🌊"),
                Province("BAS", "Basilan (Isabela City)", "Lamitan", "🥥"),
                Province("SUL", "Sulu (Jolo)", "Jolo", "🌴"),
                Province("TAW", "Tawi-Tawi (Bongao Peak)", "Bongao", "🐢")
            )
        )
    )

    fun findProvince(code: String): Province? {
        return regions.flatMap { it.provinces }.find { it.code.equals(code, ignoreCase = true) }
    }
}
