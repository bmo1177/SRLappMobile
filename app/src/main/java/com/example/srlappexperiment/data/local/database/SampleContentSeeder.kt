package com.example.srlappexperiment.data.local.database

import com.example.srlappexperiment.data.local.database.dao.VocabularyCardDao
import com.example.srlappexperiment.data.local.database.entities.VocabularyCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

object SampleContentSeeder {

    suspend fun seedSpanishContent(dao: VocabularyCardDao) {
        val totalCards = dao.getTotalCardsLearned() // Check if already seeded (simplified)
        // In real app, check if table is empty
        
        val cards = mutableListOf<VocabularyCard>()
        
        // Day 1: Greetings (15 words)
        val greetings = listOf(
            "Hola" to "Hello", "Buenos días" to "Good morning", "Buenas tardes" to "Good afternoon",
            "Buenas noches" to "Good night", "Adiós" to "Goodbye", "Por favor" to "Please",
            "Gracias" to "Thank you", "De nada" to "You're welcome", "Lo siento" to "I'm sorry",
            "¿Cómo estás?" to "How are you?", "Bien" to "Well/Fine", "Mal" to "Bad",
            "¿Cómo te llamas?" to "What is your name?", "Me llamo..." to "My name is...", "Mucho gusto" to "Nice to meet you"
        )
        cards.addAll(createCards(greetings, "Greetings", "beginner"))

        // Day 2: Family (15 words)
        val family = listOf(
            "Padre" to "Father", "Madre" to "Mother", "Hermano" to "Brother", "Hermana" to "Sister",
            "Hijo" to "Son", "Hija" to "Daughter", "Abuelo" to "Grandfather", "Abuela" to "Grandmother",
            "Tío" to "Uncle", "Tía" to "Aunt", "Primo" to "Cousin (m)", "Prima" to "Cousin (f)",
            "Esposo" to "Husband", "Esposa" to "Wife", "Familia" to "Family"
        )
        cards.addAll(createCards(family, "Family", "beginner"))

        // Day 3: Food (15 words)
        val food = listOf(
            "Comida" to "Food", "Agua" to "Water", "Pan" to "Bread", "Leche" to "Milk",
            "Fruta" to "Fruit", "Verdura" to "Vegetable", "Carne" to "Meat", "Arroz" to "Rice",
            "Queso" to "Cheese", "Huevo" to "Egg", "Cena" to "Dinner", "Almuerzo" to "Lunch",
            "Desayuno" to "Breakfast", "Restaurante" to "Restaurant", "Vino" to "Wine"
        )
        cards.addAll(createCards(food, "Food", "beginner"))

        // Day 4: Travel (15 words)
        val travel = listOf(
            "Aeropuerto" to "Airport", "Avión" to "Plane", "Tren" to "Train", "Autobús" to "Bus",
            "Boleto" to "Ticket", "Maleta" to "Suitcase", "Pasaporte" to "Passport", "Hotel" to "Hotel",
            "Calle" to "Street", "Mapa" to "Map", "Derecha" to "Right", "Izquierda" to "Left",
            "Cerca" to "Near", "Lejos" to "Far", "Viaje" to "Trip"
        )
        cards.addAll(createCards(travel, "Travel", "beginner"))

        // Day 5: Numbers/Time (15 words)
        val numbers = listOf(
            "Uno" to "One", "Dos" to "Two", "Tres" to "Three", "Diez" to "Ten",
            "Cien" to "One hundred", "Hora" to "Hour/Time", "Minuto" to "Minute", "Día" to "Day",
            "Semana" to "Week", "Mes" to "Month", "Año" to "Year", "Mañana" to "Tomorrow/Morning",
            "Hoy" to "Today", "Ayer" to "Yesterday", "Ahora" to "Now"
        )
        cards.addAll(createCards(numbers, "Numbers & Time", "beginner"))

        // Day 6: Colors/Clothes (15 words)
        val colors = listOf(
            "Rojo" to "Red", "Azul" to "Blue", "Verde" to "Green", "Amarillo" to "Yellow",
            "Blanco" to "White", "Negro" to "Black", "Ropa" to "Clothes", "Camisa" to "Shirt",
            "Pantalones" to "Pants", "Zapatos" to "Shoes", "Vestido" to "Dress", "Chaqueta" to "Jacket",
            "Sombrero" to "Hat", "Grande" to "Big", "Pequeño" to "Small"
        )
        cards.addAll(createCards(colors, "Colors & Clothes", "beginner"))

        // Day 7: Verbs (15 words)
        val verbs = listOf(
            "Ser" to "To be (permanent)", "Estar" to "To be (temporary)", "Tener" to "To have", "Hacer" to "To do/make",
            "Ir" to "To go", "Venir" to "To come", "Poder" to "To be able to", "Querer" to "To want",
            "Saber" to "To know (fact)", "Hablar" to "To speak", "Comer" to "To eat", "Beber" to "To drink",
            "Leer" to "To read", "Escribir" to "To write", "Vivir" to "To live"
        )
        cards.addAll(createCards(verbs, "Common Verbs", "beginner"))

        withContext(Dispatchers.IO) {
            dao.insertAll(cards)
        }
    }

    private fun createCards(pairs: List<Pair<String, String>>, category: String, difficulty: String): List<VocabularyCard> {
        return pairs.map { (word, trans) ->
            VocabularyCard(
                id = UUID.randomUUID().toString(),
                word = word,
                translation = trans,
                category = category,
                difficulty = difficulty
            )
        }
    }
}
