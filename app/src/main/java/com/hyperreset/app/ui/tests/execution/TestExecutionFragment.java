package com.hyperreset.app.ui.tests.execution;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.hyperreset.app.R;
import com.hyperreset.app.ui.tests.entry.ResultEntryFragment;
import java.util.HashMap;
import java.util.Map;

public class TestExecutionFragment extends Fragment {

    private boolean timerStartedByUser = false;

    private static final Map<String, String> INSTRUCCIONES = new HashMap<>();
    static {
        INSTRUCCIONES.put("ILLINOIS",
                "El Test de Illinois mide la agilidad y capacidad de cambio de dirección.\n\n" +
                "1. Colócate en la posición de salida, boca abajo.\n" +
                "2. A la señal, levántate y corre el circuito lo más rápido posible.\n" +
                "3. Sigue el recorrido marcado por los conos.\n" +
                "4. Cruza la línea de meta sin disminuir la velocidad.\n\n" +
                "El tiempo se mide en segundos. ¡Menos tiempo = mejor resultado!");

        INSTRUCCIONES.put("FLEXION_CODOS",
                "Mide la resistencia muscular de brazos y pectorales.\n\n" +
                "1. Colócate en posición de plancha (flexiones).\n" +
                "2. Baja el pecho hasta que los codos formen 90°.\n" +
                "3. Extiende los brazos completamente hacia arriba.\n" +
                "4. Realiza la MAYOR CANTIDAD posible en 30 segundos.\n\n" +
                "Solo cuentan las repeticiones completas y correctas.");

        INSTRUCCIONES.put("VELOCIDAD_20M",
                "Mide la velocidad máxima en un sprint de 20 metros.\n\n" +
                "1. Colócate en la línea de salida.\n" +
                "2. A la señal, corre los 20 metros a máxima velocidad.\n" +
                "3. No disminuyas la velocidad antes de cruzar la meta.\n\n" +
                "El tiempo se registra en segundos. ¡A menor tiempo, mejor rendimiento!");

        INSTRUCCIONES.put("VELOCIDAD_REACCION",
                "Mide tu tiempo de reacción ante un estímulo.\n\n" +
                "1. Siéntate frente al dispositivo.\n" +
                "2. Coloca el dedo sobre el botón designado.\n" +
                "3. Reacciona lo más rápido posible cuando veas la señal.\n" +
                "4. Realiza 3 intentos para obtener un promedio.\n\n" +
                "El resultado se mide en milisegundos (ms).");

        INSTRUCCIONES.put("SALTO_HORIZONTAL",
                "Mide la potencia explosiva de tus piernas.\n\n" +
                "1. Colócate detrás de la línea de salida.\n" +
                "2. Flexiona las rodillas y balancea los brazos.\n" +
                "3. Salta hacia adelante lo más lejos posible.\n" +
                "4. Cae con ambos pies y mantén el equilibrio.\n\n" +
                "Se mide la distancia desde la línea hasta el talón más retrasado.");

        INSTRUCCIONES.put("FLEXION_TRONCO",
                "Evalúa la flexibilidad de la espalda y isquiotibiales.\n\n" +
                "1. Siéntate en el suelo con las piernas extendidas.\n" +
                "2. Coloca los pies contra el cajón de medición.\n" +
                "3. Lentamente, inclínate hacia adelante sin doblar las rodillas.\n" +
                "4. Desliza las manos sobre la regla lo más lejos posible.\n" +
                "5. Mantén la posición final durante 2 segundos.\n\n" +
                "Se mide en centímetros. ¡A mayor distancia, mejor flexibilidad!");

        INSTRUCCIONES.put("DINAMOMETRIA",
                "Mide la fuerza de presión de la mano (preensión).\n\n" +
                "1. Toma el dinamómetro con la mano dominante.\n" +
                "2. Mantén el brazo estirado hacia abajo.\n" +
                "3. Aprieta el dinamómetro con la máxima fuerza posible.\n" +
                "4. Realiza 2 intentos y registra el mejor.\n\n" +
                "Se mide en kilogramos (kg) de fuerza.");

        INSTRUCCIONES.put("ANDERSEN",
                "Test de resistencia aeróbica (10 minutos).\n\n" +
                "1. Corre durante 10 minutos tratando de cubrir la mayor distancia posible.\n" +
                "2. Puedes alternar entre correr y trotar según tu capacidad.\n" +
                "3. No te detengas por completo durante la prueba.\n" +
                "4. Al finalizar, registra la distancia total recorrida.\n\n" +
                "La distancia se mide en metros. ¡A mayor distancia, mejor resistencia cardiovascular!");
    }

    private static final Map<String, String> NOMBRES_TEST = new HashMap<>();
    static {
        NOMBRES_TEST.put("ILLINOIS", "Test de Illinois");
        NOMBRES_TEST.put("FLEXION_CODOS", "Flexión/Extensión de codos");
        NOMBRES_TEST.put("VELOCIDAD_20M", "Velocidad 20 metros");
        NOMBRES_TEST.put("VELOCIDAD_REACCION", "Velocidad de reacción");
        NOMBRES_TEST.put("SALTO_HORIZONTAL", "Salto horizontal");
        NOMBRES_TEST.put("FLEXION_TRONCO", "Flexión profunda de tronco");
        NOMBRES_TEST.put("DINAMOMETRIA", "Dinamometría");
        NOMBRES_TEST.put("ANDERSEN", "Test de Andersen");
    }

    private long testId;
    private String tipoTest;
    private long deportistaId;
    private TestExecutionViewModel viewModel;

    private TextView tvTitle, tvInstructions, tvTimerDisplay, tvTimerLabel;
    private MaterialCardView cardTimer;
    private Button btnStart;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            testId = args.getLong("testId", -1);
            tipoTest = args.getString("tipoTest", "");
            deportistaId = args.getLong("deportistaId", -1);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_test_execution, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(TestExecutionViewModel.class);

        tvTitle = view.findViewById(R.id.tvExecutionTitle);
        tvInstructions = view.findViewById(R.id.tvInstructions);
        tvTimerDisplay = view.findViewById(R.id.tvTimerDisplay);
        tvTimerLabel = view.findViewById(R.id.tvTimerLabel);
        cardTimer = view.findViewById(R.id.cardTimer);
        btnStart = view.findViewById(R.id.btnStartExecution);

        // Set title
        String nombre = NOMBRES_TEST.getOrDefault(tipoTest, tipoTest);
        tvTitle.setText(nombre);

        // Set instructions
        String instrucciones = INSTRUCCIONES.getOrDefault(tipoTest,
                "Sigue las indicaciones del entrenador para completar esta prueba.");
        tvInstructions.setText(instrucciones);

        // Configure timer if applicable (don't start automatically)
        boolean hasTimer = TestExecutionViewModel.hasTimer(tipoTest);
        if (hasTimer) {
            long duration = TestExecutionViewModel.getDurationForTestType(tipoTest);
            viewModel.setupTimer(duration);
            cardTimer.setVisibility(View.VISIBLE);
            btnStart.setText(R.string.test_execution_start);
        } else {
            cardTimer.setVisibility(View.GONE);
            btnStart.setText(R.string.test_execution_next);
        }

        // Enable button after 3 seconds
        viewModel.enableProceedAfterDelay();

        // Observe timer display
        viewModel.getTimerDisplay().observe(getViewLifecycleOwner(), display -> {
            tvTimerDisplay.setText(display);
        });

        // Observe canProceed — update button enabled state
        viewModel.getCanProceed().observe(getViewLifecycleOwner(), can -> {
            btnStart.setEnabled(can != null && can);
        });

        // Observe timer running state — update button text when timer is active
        viewModel.getIsTimerRunning().observe(getViewLifecycleOwner(), running -> {
            if (running != null && running) {
                btnStart.setEnabled(false);
                btnStart.setText(R.string.test_execution_running);
            }
        });

        // Observe timer finished — change button to "Siguiente"
        viewModel.getTimerFinished().observe(getViewLifecycleOwner(), finished -> {
            if (finished != null && finished) {
                btnStart.setText(R.string.test_execution_next);
            }
        });

        // Button click logic
        btnStart.setOnClickListener(v -> {
            if (hasTimer && !timerStartedByUser) {
                // First click: start the timer
                timerStartedByUser = true;
                viewModel.startTimer();
            } else {
                // Timer already finished or no timer: navigate
                viewModel.cancelTimer();
                navigateToResultEntry();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // Intercept back press to show confirmation dialog
        requireView().setOnKeyListener((v, keyCode, event) -> {
            if (keyCode == android.view.KeyEvent.KEYCODE_BACK && event.getAction() == android.view.KeyEvent.ACTION_UP) {
                showCancelDialog();
                return true;
            }
            return false;
        });
    }

    private void showCancelDialog() {
        new AlertDialog.Builder(requireContext())
                .setTitle(R.string.test_execution_cancel_title)
                .setMessage(R.string.test_execution_cancel_message)
                .setPositiveButton(R.string.test_execution_cancel_confirm, (dialog, which) -> {
                    viewModel.cancelTimer();
                    requireActivity().getSupportFragmentManager().popBackStack();
                })
                .setNegativeButton(R.string.test_execution_cancel_keep, null)
                .show();
    }

    private void navigateToResultEntry() {
        ResultEntryFragment fragment = new ResultEntryFragment();
        Bundle args = new Bundle();
        args.putLong("testId", testId);
        args.putString("tipoTest", tipoTest);
        fragment.setArguments(args);
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.cancelTimer();
    }
}
