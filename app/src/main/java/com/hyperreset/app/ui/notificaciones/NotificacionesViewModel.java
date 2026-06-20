package com.hyperreset.app.ui.notificaciones;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.hyperreset.app.R;
import com.hyperreset.app.data.model.ActividadRecienteItem;
import com.hyperreset.app.data.model.NotificacionItem;
import com.hyperreset.app.data.repository.DashboardRepository;
import com.hyperreset.app.utils.Resource;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for the Notificaciones screen.
 * Fetches data from DashboardRepository.getActividad() and maps
 * ActividadRecienteItem objects to NotificacionItem UI models.
 */
public class NotificacionesViewModel extends ViewModel {

    private final DashboardRepository repository;

    private final MutableLiveData<Resource<List<NotificacionItem>>> notificaciones = new MutableLiveData<>();

    public NotificacionesViewModel() {
        this.repository = new DashboardRepository();
    }

    public NotificacionesViewModel(DashboardRepository repository) {
        this.repository = repository;
    }

    public LiveData<Resource<List<NotificacionItem>>> getNotificaciones() {
        return notificaciones;
    }

    public void loadNotificaciones() {
        notificaciones.setValue(Resource.loading());
        repository.getActividad(0, 20, result -> {
            if (result.status == Resource.Status.SUCCESS && result.data != null) {
                List<NotificacionItem> items = mapToNotificaciones(result.data.getItems());
                notificaciones.setValue(Resource.success(items));
            } else if (result.status == Resource.Status.ERROR) {
                notificaciones.setValue(Resource.error(
                    result.message != null ? result.message : "Error al cargar notificaciones"));
            } else {
                notificaciones.setValue(Resource.error("Error desconocido"));
            }
        });
    }

    /**
     * Maps ActividadRecienteItem objects to NotificacionItem UI models.
     * Uses the tipo field to determine icon and title.
     */
    private List<NotificacionItem> mapToNotificaciones(List<ActividadRecienteItem> actividad) {
        List<NotificacionItem> items = new ArrayList<>();
        if (actividad == null) return items;

        for (ActividadRecienteItem a : actividad) {
            if (a == null) continue;

            String tipo = a.getTipo() != null ? a.getTipo() : "info";
            String paciente = a.getPacienteNombre() != null ? a.getPacienteNombre() : "";
            String accion = a.getAccion() != null ? a.getAccion() : "";

            int iconResId;
            String title;

            switch (tipo.toLowerCase()) {
                case "success":
                    iconResId = R.drawable.ic_bell;
                    title = "Nuevo progreso";
                    break;
                case "warning":
                    iconResId = R.drawable.ic_bell;
                    title = "Alerta";
                    break;
                case "info":
                    iconResId = R.drawable.ic_notification;
                    title = "Información";
                    break;
                case "appointment":
                    iconResId = R.drawable.ic_calendar;
                    title = "Próxima cita";
                    break;
                case "message":
                    iconResId = R.drawable.ic_message;
                    title = "Nuevo mensaje";
                    break;
                default:
                    iconResId = R.drawable.ic_notification;
                    title = "Notificación";
                    break;
            }

            String description = paciente.isEmpty() ? accion : paciente + " - " + accion;
            if (description.isEmpty()) description = "Nueva actividad";

            items.add(new NotificacionItem(iconResId, title, description, a.getTimestamp(), tipo));
        }

        return items;
    }
}
