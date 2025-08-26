package com.mastermind.services;

import com.mastermind.models.NumCombination;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@DisplayName("RandomNumberApiClient")
class RandomNumberApiClientTest {

    @Mock
    private HttpClient mockHttpClient;
    
    @Mock
    private HttpResponse<String> mockResponse;
    
    private RandomNumberApiClient apiClient;
    private AutoCloseable mockCloseable;

    @BeforeEach
    void setUp() {
        mockCloseable = MockitoAnnotations.openMocks(this);
        apiClient = new RandomNumberApiClient(mockHttpClient);
    }

    @AfterEach
    void tearDown() throws Exception {
        mockCloseable.close();
    }

    @Nested
    @DisplayName("Constructor tests")
    class ConstructorTests {

        @Test
        @DisplayName("should create client with default HttpClient")
        void shouldCreateClientWithDefaultHttpClient() {
            // Act & Assert
            assertDoesNotThrow(() -> new RandomNumberApiClient());
        }

        @Test
        @DisplayName("should create client with injected HttpClient")
        void shouldCreateClientWithInjectedHttpClient() {
            // Arrange
            HttpClient customClient = mock(HttpClient.class);

            // Act & Assert
            assertDoesNotThrow(() -> new RandomNumberApiClient(customClient));
        }
    }

    @Nested
    @DisplayName("Successful API responses")
    class SuccessfulApiResponses {

        @Test
        @DisplayName("should parse valid API response correctly")
        void shouldParseValidApiResponseCorrectly() throws IOException, InterruptedException {
            // Arrange
            String validResponse = "0\n1\n2\n3";
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(validResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);

            // Act
            NumCombination result = apiClient.getRandomNums();

            // Assert
            assertNotNull(result);
            assertEquals(Arrays.asList(0, 1, 2, 3), result.getNumbers());
            assertEquals(4, result.getNumbers().size());
        }

        @Test
        @DisplayName("should handle response with duplicate numbers")
        void shouldHandleResponseWithDuplicateNumbers() throws IOException, InterruptedException {
            // Arrange
            String responseWithDuplicates = "1\n1\n2\n2";
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(responseWithDuplicates);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);

            // Act
            NumCombination result = apiClient.getRandomNums();

            // Assert
            assertNotNull(result);
            assertEquals(Arrays.asList(1, 1, 2, 2), result.getNumbers());
            assertEquals(4, result.getNumbers().size());
        }

        @Test
        @DisplayName("should handle response with maximum values")
        void shouldHandleResponseWithMaximumValues() throws IOException, InterruptedException {
            // Arrange
            String maxValueResponse = "7\n7\n7\n7";
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(maxValueResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);

            // Act
            NumCombination result = apiClient.getRandomNums();

            // Assert
            assertNotNull(result);
            assertEquals(Arrays.asList(7, 7, 7, 7), result.getNumbers());
        }

        @Test
        @DisplayName("should handle response with minimum values")
        void shouldHandleResponseWithMinimumValues() throws IOException, InterruptedException {
            // Arrange
            String minValueResponse = "0\n0\n0\n0";
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(minValueResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);

            // Act
            NumCombination result = apiClient.getRandomNums();

            // Assert
            assertNotNull(result);
            assertEquals(Arrays.asList(0, 0, 0, 0), result.getNumbers());
        }

        @Test
        @DisplayName("should trim whitespace from response")
        void shouldTrimWhitespaceFromResponse() throws IOException, InterruptedException {
            // Arrange
            String responseWithWhitespace = "  \n0\n1\n2\n3\n  ";
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(responseWithWhitespace);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);

            // Act
            NumCombination result = apiClient.getRandomNums();

            // Assert
            assertNotNull(result);
            assertEquals(Arrays.asList(0, 1, 2, 3), result.getNumbers());
        }
    }

    @Nested
    @DisplayName("HTTP request verification")
    class HttpRequestVerification {

        @Test
        @DisplayName("should make GET request to correct URL")
        void shouldMakeGetRequestToCorrectUrl() throws IOException, InterruptedException {
            // Arrange
            String validResponse = "1\n2\n3\n4";
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(validResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);

            // Act
            apiClient.getRandomNums();

            // Assert
            verify(mockHttpClient, times(1)).send(
                    argThat(request -> 
                            request.uri().toString().contains("random.org/integers") &&
                            request.uri().toString().contains("num=4") &&
                            request.uri().toString().contains("min=0") &&
                            request.uri().toString().contains("max=7") &&
                            request.method().equals("GET")
                    ),
                    any(HttpResponse.BodyHandler.class)
            );
        }
    }

    @Nested
    @DisplayName("Error handling")
    class ErrorHandling {

        @Test
        @DisplayName("should throw RandomNumberApiException when IOException occurs")
        void shouldThrowRandomNumberApiExceptionWhenIOExceptionOccurs() throws IOException, InterruptedException {
            // Arrange
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new IOException("Network error"));

            // Act & Assert
            RandomNumberApiException exception = assertThrows(RandomNumberApiException.class, 
                    () -> apiClient.getRandomNums());

            assertInstanceOf(IOException.class, exception.getCause());
            assertEquals("Network error", exception.getCause().getMessage());
            assertTrue(exception.getMessage().contains("Network error while contacting Random.org API"));
        }

        @Test
        @DisplayName("should throw RandomNumberApiException when InterruptedException occurs")
        void shouldThrowRandomNumberApiExceptionWhenInterruptedExceptionOccurs() throws IOException, InterruptedException {
            // Arrange
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenThrow(new InterruptedException("Thread interrupted"));

            // Act & Assert
            RandomNumberApiException exception = assertThrows(RandomNumberApiException.class, 
                    () -> apiClient.getRandomNums());

            assertInstanceOf(InterruptedException.class, exception.getCause());
            assertEquals("Thread interrupted", exception.getCause().getMessage());
            assertTrue(exception.getMessage().contains("Network error while contacting Random.org API"));
        }

        @Test
        @DisplayName("should handle malformed response numbers")
        void shouldHandleMalformedResponseNumbers() throws IOException, InterruptedException {
            // Arrange
            String malformedResponse = "0\nabc\n2\n3";
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(malformedResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);

            // Act & Assert
            assertThrows(NumberFormatException.class, () -> apiClient.getRandomNums());
        }
    }

    @Nested
    @DisplayName("Response parsing edge cases")
    class ResponseParsingEdgeCases {

        @Test
        @DisplayName("should handle empty response body")
        void shouldHandleEmptyResponseBody() throws IOException, InterruptedException {
            // Arrange
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn("");
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);

            // Act & Assert
            // This should throw an exception when trying to create NumCombination with wrong size
            assertThrows(IllegalArgumentException.class, () -> apiClient.getRandomNums());
        }

        @Test
        @DisplayName("should handle response with wrong number count")
        void shouldHandleResponseWithWrongNumberCount() throws IOException, InterruptedException {
            // Arrange
            String wrongCountResponse = "1\n2\n3"; // Only 3 numbers instead of 4
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(wrongCountResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);

            // Act & Assert
            // NumCombination constructor should throw exception for wrong count
            assertThrows(IllegalArgumentException.class, () -> apiClient.getRandomNums());
        }

        @Test
        @DisplayName("should handle response with numbers outside valid range")
        void shouldHandleResponseWithNumbersOutsideValidRange() throws IOException, InterruptedException {
            // Arrange
            String outOfRangeResponse = "0\n1\n2\n8"; // 8 is outside 0-7 range
            when(mockResponse.statusCode()).thenReturn(200);
            when(mockResponse.body()).thenReturn(outOfRangeResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);

            // Act & Assert
            // NumCombination constructor should validate range
            assertThrows(IllegalArgumentException.class, () -> apiClient.getRandomNums());
        }
    }
    
    @Nested
    @DisplayName("HTTP status code handling")
    class HttpStatusCodeHandling {
        
        @Test
        @DisplayName("should handle 503 Service Unavailable with error message")
        void shouldHandle503ServiceUnavailableWithErrorMessage() throws IOException, InterruptedException {
            // Arrange
            String errorResponse = "Error: Server too busy right now, please back off";
            when(mockResponse.statusCode()).thenReturn(503);
            when(mockResponse.body()).thenReturn(errorResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);

            // Act & Assert
            RandomNumberApiException exception = assertThrows(RandomNumberApiException.class,
                    () -> apiClient.getRandomNums());
            
            assertTrue(exception.getMessage().contains("Random.org API error"));
            assertTrue(exception.getMessage().contains("Error: Server too busy right now"));
        }
        
        @Test
        @DisplayName("should handle 503 response without Error prefix")
        void shouldHandle503ResponseWithoutErrorPrefix() throws IOException, InterruptedException {
            // Arrange
            String unexpectedErrorResponse = "Something went wrong";
            when(mockResponse.statusCode()).thenReturn(503);
            when(mockResponse.body()).thenReturn(unexpectedErrorResponse);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);

            // Act & Assert
            RandomNumberApiException exception = assertThrows(RandomNumberApiException.class,
                    () -> apiClient.getRandomNums());
            
            assertTrue(exception.getMessage().contains("Random.org API error"));
            assertTrue(exception.getMessage().contains("Unknown error from Random.org API"));
        }
        
        @Test
        @DisplayName("should handle unexpected HTTP status codes")
        void shouldHandleUnexpectedHttpStatusCodes() throws IOException, InterruptedException {
            // Arrange - Test 404 Not Found
            when(mockResponse.statusCode()).thenReturn(404);
            when(mockResponse.body()).thenReturn("Not Found");
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);

            // Act & Assert
            RandomNumberApiException exception = assertThrows(RandomNumberApiException.class,
                    () -> apiClient.getRandomNums());
            
            assertTrue(exception.getMessage().contains("Unexpected HTTP status: 404"));
            assertTrue(exception.getMessage().contains("Not Found"));
        }
        
        @Test
        @DisplayName("should handle 500 Internal Server Error")
        void shouldHandle500InternalServerError() throws IOException, InterruptedException {
            // Arrange
            when(mockResponse.statusCode()).thenReturn(500);
            when(mockResponse.body()).thenReturn("Internal Server Error");
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);

            // Act & Assert
            RandomNumberApiException exception = assertThrows(RandomNumberApiException.class,
                    () -> apiClient.getRandomNums());
            
            assertTrue(exception.getMessage().contains("Unexpected HTTP status: 500"));
            assertTrue(exception.getMessage().contains("Internal Server Error"));
        }
        
        @Test
        @DisplayName("should handle null response body in 503 error")
        void shouldHandleNullResponseBodyIn503Error() throws IOException, InterruptedException {
            // Arrange
            when(mockResponse.statusCode()).thenReturn(503);
            when(mockResponse.body()).thenReturn(null);
            when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                    .thenReturn(mockResponse);

            // Act & Assert
            RandomNumberApiException exception = assertThrows(RandomNumberApiException.class,
                    () -> apiClient.getRandomNums());
            
            assertTrue(exception.getMessage().contains("Random.org API error"));
            assertTrue(exception.getMessage().contains("Unknown error from Random.org API"));
        }
    }
}