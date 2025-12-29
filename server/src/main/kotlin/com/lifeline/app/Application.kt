package com.lifeline.app

import com.lifeline.app.AppContainer
import com.lifeline.app.database.DatabaseDriverFactory
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.Serializable
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first

const val SERVER_PORT = 8080

fun main() {
    val port = System.getProperty("server.port")?.toIntOrNull()
        ?: System.getenv("PORT")?.toIntOrNull()
        ?: SERVER_PORT

    embeddedServer(Netty, port = port, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // Initialize the app container with database and HTTP client
    val appContainer = AppContainer(DatabaseDriverFactory())
    
    install(ContentNegotiation) {
        json()
    }
    
    routing {
        get("/") {
            call.respondText("LifelineAI Server API")
        }
        
        route("/api/v1") {
            route("/ai") {
                post("/chat") {
                    try {
                        val request = call.receive<ChatRequest>()
                        // Use the AI client from app container
                        val response = runBlocking {
                            appContainer.aiClient.processRequest(request.prompt, request.context.mapValues { it.value })
                        }
                        call.respond(
                            ChatResponse(
                                text = response.text,
                                confidence = response.confidence,
                                source = response.source.name
                            )
                        )
                    } catch (e: Exception) {
                        call.respond(
                            status = io.ktor.http.HttpStatusCode.BadRequest,
                            message = mapOf("error" to (e.message ?: "Unknown error"))
                        )
                    }
                }
                
                get("/health") {
                    call.respond(mapOf("status" to "ok", "service" to "ai"))
                }
            }
            
            route("/health") {
                get("/symptoms") {
                    try {
                        val symptoms = runBlocking {
                            appContainer.healthRepository.getSymptoms(null, null).first()
                        }
                        call.respond(symptoms)
                    } catch (e: Exception) {
                        call.respond(
                            status = io.ktor.http.HttpStatusCode.InternalServerError,
                            message = mapOf("error" to (e.message ?: "Database error"))
                        )
                    }
                }
                
                get("/timeline") {
                    try {
                        val timeline = runBlocking {
                            appContainer.healthRepository.getTimelineEntries(null, null).first()
                        }
                        call.respond(timeline)
                    } catch (e: Exception) {
                        call.respond(
                            status = io.ktor.http.HttpStatusCode.InternalServerError,
                            message = mapOf("error" to (e.message ?: "Database error"))
                        )
                    }
                }
            }
            
            route("/finance") {
                get("/transactions") {
                    try {
                        val transactions = runBlocking {
                            appContainer.financeRepository.getTransactions(null, null)
                        }
                        call.respond(transactions)
                    } catch (e: Exception) {
                        call.respond(
                            status = io.ktor.http.HttpStatusCode.InternalServerError,
                            message = mapOf("error" to (e.message ?: "Database error"))
                        )
                    }
                }
                
                get("/goals") {
                    try {
                        val goals = runBlocking {
                            appContainer.financeRepository.getGoals()
                        }
                        call.respond(goals)
                    } catch (e: Exception) {
                        call.respond(
                            status = io.ktor.http.HttpStatusCode.InternalServerError,
                            message = mapOf("error" to (e.message ?: "Database error"))
                        )
                    }
                }
            }
            
            route("/learning") {
                get("/goals") {
                    try {
                        val goals = runBlocking {
                            appContainer.learningRepository.getGoals()
                        }
                        call.respond(goals)
                    } catch (e: Exception) {
                        call.respond(
                            status = io.ktor.http.HttpStatusCode.InternalServerError,
                            message = mapOf("error" to (e.message ?: "Database error"))
                        )
                    }
                }
                
                get("/modules") {
                    try {
                        val modules = runBlocking {
                            appContainer.learningRepository.getModules()
                        }
                        call.respond(modules)
                    } catch (e: Exception) {
                        call.respond(
                            status = io.ktor.http.HttpStatusCode.InternalServerError,
                            message = mapOf("error" to (e.message ?: "Database error"))
                        )
                    }
                }
            }
            
            route("/services") {
                get {
                    try {
                        val services = runBlocking {
                            appContainer.servicesRepository.getServices()
                        }
                        call.respond(services)
                    } catch (e: Exception) {
                        call.respond(
                            status = io.ktor.http.HttpStatusCode.InternalServerError,
                            message = mapOf("error" to (e.message ?: "Database error"))
                        )
                    }
                }
                
                get("/search") {
                    try {
                        val query = call.request.queryParameters["q"] ?: ""
                        val results = runBlocking {
                            appContainer.servicesRepository.searchServices(query)
                        }
                        call.respond(results)
                    } catch (e: Exception) {
                        call.respond(
                            status = io.ktor.http.HttpStatusCode.InternalServerError,
                            message = mapOf("error" to (e.message ?: "Database error"))
                        )
                    }
                }
            }
        }
    }
}

@Serializable
data class ChatRequest(
    val prompt: String,
    val context: Map<String, String> = emptyMap()
)

@Serializable
data class ChatResponse(
    val text: String,
    val confidence: Float,
    val source: String
)