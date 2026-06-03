package com.hyperreset.app.data.api;

import com.hyperreset.app.data.model.ApiResponse;
import com.hyperreset.app.data.model.AuthResponse;
import com.hyperreset.app.data.model.CitaResponse;
import com.hyperreset.app.data.model.CoachResponse;
import com.hyperreset.app.data.model.DashboardCoachResponse;
import com.hyperreset.app.data.model.DashboardDeportistaResponse;
import com.hyperreset.app.data.model.DeportistaResponse;
import com.hyperreset.app.data.model.LoginRequest;
import com.hyperreset.app.data.model.MaterialResponse;
import com.hyperreset.app.data.model.MensajeResponse;
import com.hyperreset.app.data.model.RegisterRequest;
import com.hyperreset.app.data.model.ReporteResponse;
import com.hyperreset.app.data.model.ResultadoResponse;
import com.hyperreset.app.data.model.TestFisicoResponse;
import com.hyperreset.app.data.model.TipoTestEstadoResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Retrofit API interface matching all backend controller endpoints.
 * All responses are wrapped in {@link ApiResponse<T>}.
 */
public interface ApiService {

    // ==================================================================
    // Auth — /api/auth
    // ==================================================================

    @POST("auth/login")
    Call<ApiResponse<AuthResponse>> login(@Body LoginRequest request);

    @POST("auth/register")
    Call<ApiResponse<AuthResponse>> register(@Body RegisterRequest request);

    @GET("auth/profile")
    Call<ApiResponse<AuthResponse>> getProfile();

    // ==================================================================
    // Deportistas — /api/deportistas
    // ==================================================================

    @GET("deportistas")
    Call<ApiResponse<List<DeportistaResponse>>> getDeportistas();

    @GET("deportistas/{id}")
    Call<ApiResponse<DeportistaResponse>> getDeportista(@Path("id") long id);

    @GET("deportistas/coach/{coachId}")
    Call<ApiResponse<List<DeportistaResponse>>> getDeportistasByCoach(@Path("coachId") long coachId);

    @GET("deportistas/coaches")
    Call<ApiResponse<List<CoachResponse>>> getCoaches();

    @POST("deportistas")
    Call<ApiResponse<DeportistaResponse>> createDeportista(@Body Object request);

    @PUT("deportistas/{id}")
    Call<ApiResponse<DeportistaResponse>> updateDeportista(@Path("id") long id, @Body Object request);

    // ==================================================================
    // Test Físicos — /api/test-fisicos
    // ==================================================================

    @GET("test-fisicos")
    Call<ApiResponse<List<TestFisicoResponse>>> getTestFisicos();

    @GET("test-fisicos/{id}")
    Call<ApiResponse<TestFisicoResponse>> getTestFisico(@Path("id") long id);

    @GET("test-fisicos/deportista/{deportistaId}")
    Call<ApiResponse<List<TestFisicoResponse>>> getTestFisicosByDeportista(@Path("deportistaId") long deportistaId);

    @POST("test-fisicos")
    Call<ApiResponse<TestFisicoResponse>> createTestFisico(@Body Object request);

    @PUT("test-fisicos/{id}")
    Call<ApiResponse<TestFisicoResponse>> updateTestFisico(@Path("id") long id, @Body Object request);

    @PATCH("test-fisicos/{id}/completar")
    Call<ApiResponse<TestFisicoResponse>> completarTest(@Path("id") long id);

    // ==================================================================
    // Resultados — /api/resultados
    // ==================================================================

    @GET("resultados/test/{testId}")
    Call<ApiResponse<List<ResultadoResponse>>> getResultadosByTest(@Path("testId") long testId);

    @GET("resultados/deportista/{deportistaId}")
    Call<ApiResponse<List<ResultadoResponse>>> getResultadosByDeportista(@Path("deportistaId") long deportistaId);

    @POST("resultados")
    Call<ApiResponse<ResultadoResponse>> createResultado(@Body Object request);

    // ==================================================================
    // Citas — /api/citas
    // ==================================================================

    @GET("citas")
    Call<ApiResponse<List<CitaResponse>>> getCitas();

    @GET("citas/{id}")
    Call<ApiResponse<CitaResponse>> getCita(@Path("id") long id);

    @GET("citas/coach/{coachId}")
    Call<ApiResponse<List<CitaResponse>>> getCitasByCoach(@Path("coachId") long coachId);

    @GET("citas/deportista/{deportistaId}")
    Call<ApiResponse<List<CitaResponse>>> getCitasByDeportista(@Path("deportistaId") long deportistaId);

    @GET("citas/rango")
    Call<ApiResponse<List<CitaResponse>>> getCitasByDateRange(
            @Query("start") String start,
            @Query("end") String end);

    @POST("citas")
    Call<ApiResponse<CitaResponse>> createCita(@Body Object request);

    @PUT("citas/{id}")
    Call<ApiResponse<CitaResponse>> updateCita(@Path("id") long id, @Body Object request);

    @DELETE("citas/{id}")
    Call<ApiResponse<Void>> deleteCita(@Path("id") long id);

    @PATCH("citas/{id}/estado")
    Call<ApiResponse<CitaResponse>> updateCitaEstado(@Path("id") long id, @Query("estado") String estado);

    // ==================================================================
    // Mensajes — /api/mensajes
    // ==================================================================

    @POST("mensajes")
    Call<ApiResponse<MensajeResponse>> sendMensaje(@Body Object request);

    @GET("mensajes/conversacion")
    Call<ApiResponse<List<MensajeResponse>>> getConversacion(@Query("with") long otherUserId);

    @GET("mensajes/recibidos")
    Call<ApiResponse<List<MensajeResponse>>> getMensajesRecibidos();

    @GET("mensajes/enviados")
    Call<ApiResponse<List<MensajeResponse>>> getMensajesEnviados();

    @PATCH("mensajes/{id}/leer")
    Call<ApiResponse<Void>> marcarMensajeLeido(@Path("id") long id);

    @GET("mensajes/no-leidos")
    Call<ApiResponse<Integer>> getNoLeidos();

    // ==================================================================
    // Reportes — /api/reportes
    // ==================================================================

    @GET("reportes/{id}")
    Call<ApiResponse<ReporteResponse>> getReporte(@Path("id") long id);

    @GET("reportes/deportista/{deportistaId}")
    Call<ApiResponse<List<ReporteResponse>>> getReportesByDeportista(@Path("deportistaId") long deportistaId);

    @GET("reportes/test/{testId}")
    Call<ApiResponse<List<ReporteResponse>>> getReportesByTest(@Path("testId") long testId);

    @POST("reportes")
    Call<ApiResponse<ReporteResponse>> generarReporte(@Body Object request);

    // ==================================================================
    // Materiales — /api/materiales
    // ==================================================================

    @GET("materiales")
    Call<ApiResponse<List<MaterialResponse>>> getMateriales();

    @GET("materiales/{id}")
    Call<ApiResponse<MaterialResponse>> getMaterial(@Path("id") long id);

    @POST("materiales")
    Call<ApiResponse<MaterialResponse>> createMaterial(@Body Object request);

    @PUT("materiales/{id}")
    Call<ApiResponse<MaterialResponse>> updateMaterial(@Path("id") long id, @Body Object request);

    @DELETE("materiales/{id}")
    Call<ApiResponse<Void>> deleteMaterial(@Path("id") long id);

    // ==================================================================
    // Dashboard — /api/dashboard
    // ==================================================================

    @GET("dashboard/deportista/{id}")
    Call<ApiResponse<DashboardDeportistaResponse>> getDashboardDeportista(@Path("id") long id);

    @GET("dashboard/coach/{id}")
    Call<ApiResponse<DashboardCoachResponse>> getDashboardCoach(@Path("id") long id);

    // ==================================================================
    // Resultados — Tipos de Test con Estado
    // ==================================================================

    @GET("resultados/tipos-con-estado/{deportistaId}")
    Call<ApiResponse<List<TipoTestEstadoResponse>>> getTiposTestConEstado(@Path("deportistaId") long deportistaId);
}
