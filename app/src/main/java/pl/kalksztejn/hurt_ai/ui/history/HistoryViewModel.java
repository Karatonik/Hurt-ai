package pl.kalksztejn.hurt_ai.ui.history;

import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import pl.kalksztejn.hurt_ai.model.Hurt;
import pl.kalksztejn.hurt_ai.service.AuthenticationService;
import pl.kalksztejn.hurt_ai.service.FirebaseHurtService;

public class HistoryViewModel extends ViewModel {

    private final FirebaseHurtService firebaseHurtService;
    private final MutableLiveData<List<Hurt>> recordList;

    public HistoryViewModel() {
        this.firebaseHurtService = new FirebaseHurtService();
        recordList = new MutableLiveData<>();
        // Inicjalizacja danych historii rekordów
        loadRecordHistory();
    }

    public LiveData<List<Hurt>> getRecordList() {
        return recordList;
    }

    private void loadRecordHistory() {
        String owner = AuthenticationService.getOwner();
        firebaseHurtService.getRecordsByOwner(owner, new FirebaseHurtService.RecordListCallback() {
            @Override
            public void onRecordListReceived(List<Hurt> records) {
                List<Hurt> history = convertHurtListToRecordList(records);
                recordList.postValue(history);
            }

            @Override
            public void onCancelled(String errorMessage) {
            }
        });
    }

    private List<Hurt> convertHurtListToRecordList(List<Hurt> hurtList) {
        List<Hurt> recordList = new ArrayList<>();
        for (Hurt hurt : hurtList) {
            Hurt record = new Hurt(hurt.getId(), hurt.getOwner(), hurt.getImage64Base(), hurt.getData());
            recordList.add(record);
        }
        return recordList;
    }
}