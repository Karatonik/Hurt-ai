package pl.kalksztejn.hurt_ai.ui.history;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import pl.kalksztejn.hurt_ai.adapter.HistoryAdapter;
import pl.kalksztejn.hurt_ai.databinding.FragmentHistoryBinding;
import pl.kalksztejn.hurt_ai.model.Hurt;
import pl.kalksztejn.hurt_ai.service.FirebaseHurtService;

public class HistoryFragment extends Fragment implements HistoryAdapter.OnRecordDeleteListener {

    private FragmentHistoryBinding binding;
    private FirebaseHurtService firebaseHurtService;
    private HistoryAdapter adapter;

    @SuppressLint("NotifyDataSetChanged")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HistoryViewModel historyViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        firebaseHurtService = new FirebaseHurtService();

        final RecyclerView recyclerView = binding.recyclerViewHistory;

        // Ustawienie managera i adaptera dla RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setHasFixedSize(true);
        adapter = new HistoryAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnRecordDeleteListener(this);

        // Obserwuj zmiany w liście rekordów
        historyViewModel.getRecordList().observe(getViewLifecycleOwner(), records -> {
            if (records != null && !records.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                adapter.setRecordList(records);
                adapter.notifyDataSetChanged();
            } else {
                recyclerView.setVisibility(View.GONE);
            }
        });


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    @Override
    public void onRecordDelete(Hurt record) {
        firebaseHurtService.deleteRecordById(record.getId(), new FirebaseHurtService.RecordDeleteCallback() {
            @Override
            public void onRecordDeleted(String recordId) {
                // Rekord został pomyślnie usunięty
                // Wyświetlenie Toast informującego o pomyślnym usunięciu
                int position = adapter.getPosition(recordId);
                if (position != -1) {
                    adapter.removeItem(position);
                }
                Toast.makeText(getContext(), "Record deleted successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onDeleteFailure(String errorMessage) {
                // Wystąpił błąd podczas usuwania rekordu
                // Wyświetlenie Toast informującego o błędzie
                Toast.makeText(getContext(), "Failed to delete record: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}