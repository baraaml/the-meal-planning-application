// Location: network/ApiMealDiagnostic.kt
package com.example.mealflow.network

import android.util.Log
import com.example.mealflow.data.model.MealResponse
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

class ApiMealDiagnostic {
    private val client = ApiClient.client
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getRecommendedMeals(): Result<MealResponse> {
        return try {
            val response = client.get(ApiClient.Endpoints.RECOMMENDED_MEALS)
            val responseText = response.bodyAsText()

            // Log the raw response for debugging
            Log.d("DiagnosticMealApi", "API Response: $responseText")

            if (response.status.isSuccess()) {
                try {
                    val mealResponse = response.body<MealResponse>()
                    Log.d("DiagnosticMealApi", "Successfully parsed ${mealResponse.data.size} meals")
                    Result.success(mealResponse)
                } catch (e: Exception) {
                    Log.e("DiagnosticMealApi", "Error parsing response body", e)
                    diagnoseResponseIssues(responseText)
                    Result.failure(e)
                }
            } else {
                // Try to parse error response
                try {
                    diagnoseErrorResponse(responseText, response.status.value, response.status.description)
                } catch (e: Exception) {
                    Log.e("DiagnosticMealApi", "Error parsing error response", e)
                }
                Result.failure(Exception("API error: ${response.status.value} - ${response.status.description}"))
            }
        } catch (e: Exception) {
            Log.e("DiagnosticMealApi", "Error fetching meals", e)
            Result.failure(e)
        }
    }

    private fun diagnoseResponseIssues(responseText: String) {
        try {
            // Try to check the structure of the JSON
            val jsonElement = json.parseToJsonElement(responseText)
            if (jsonElement is JsonObject) {
                val jsonObject = jsonElement.jsonObject

                // Check if data field exists and what type it is
                if (jsonObject.containsKey("data")) {
                    val dataElement = jsonObject["data"]
                    Log.d("DiagnosticMealApi", "Data element exists but couldn't be parsed correctly. Type: ${dataElement?.javaClass?.simpleName}")
                } else {
                    Log.d("DiagnosticMealApi", "Response doesn't contain 'data' field")
                }

                // Log all top-level keys to help diagnose structure issues
                Log.d("DiagnosticMealApi", "Response contains keys: ${jsonObject.keys.joinToString()}")
            }
        } catch (e: Exception) {
            Log.e("DiagnosticMealApi", "Response is not valid JSON", e)
        }
    }

    private fun diagnoseErrorResponse(errorBodyString: String, statusCode: Int, statusMessage: String) {
        try {
            // Try to parse as JSON to check format
            val jsonElement = json.parseToJsonElement(errorBodyString)

            if (jsonElement is JsonObject) {
                val jsonObject = jsonElement.jsonObject

                // Check for common error patterns
                if (jsonObject.containsKey("error")) {
                    Log.d("DiagnosticMealApi", "Error message from API: ${jsonObject["error"]}")
                } else if (jsonObject.containsKey("message")) {
                    Log.d("DiagnosticMealApi", "Message from API: ${jsonObject["message"]}")
                }
            }
        } catch (e: Exception) {
            Log.e("DiagnosticMealApi", "Response is not valid JSON", e)
        }

        // Log HTTP status info
        Log.d("DiagnosticMealApi", "HTTP Status: $statusCode - $statusMessage")
    }
}