package pl.kalksztejn.hurt_ai.adapter;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import pl.kalksztejn.hurt_ai.R;
import pl.kalksztejn.hurt_ai.model.Hurt;
import pl.kalksztejn.hurt_ai.parser.JsonParser;
import pl.kalksztejn.hurt_ai.utils.ImageUtils;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    public interface OnRecordDeleteListener {
        void onRecordDelete(Hurt record);
    }
    private OnRecordDeleteListener deleteListener;

    public void setOnRecordDeleteListener(OnRecordDeleteListener listener) {
        this.deleteListener = listener;
    }

    private List<Hurt> recordList;

    @SuppressLint("NotifyDataSetChanged")
    public void setRecordList(List<Hurt> recordList) {
        this.recordList = recordList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Hurt record = recordList.get(position);
        // Ustaw dane rekordu w widoku ViewHolder
        holder.bind(record);
    }
    public void removeItem(int position) {
        if (position >= 0 && position < recordList.size()) {
            recordList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemCount() {
        return recordList != null ? recordList.size() : 0;
    }

    public int getPosition(String recordId) {
        for (int i = 0; i < recordList.size(); i++) {
            Hurt record = recordList.get(i);
            if (record.getId().equals(recordId)) {
                return i;
            }
        }
        return RecyclerView.NO_POSITION;
    }
    public class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageView;
        private final TextView textView;

        private String id = "";

        private Button deleteButton;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_history);
            textView = itemView.findViewById(R.id.text_history);
            deleteButton = itemView.findViewById(R.id.button_delete);
            id = "" ;

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Hurt record = recordList.get(position);
                        if (deleteListener != null) {
                            deleteListener.onRecordDelete(record);
                        }
                    }
                }
            });
        }

        public void bind(Hurt hurt) {
            imageView.setImageBitmap(ImageUtils.base64ToBitmap(hurt.getImage64Base()));
            textView.setText(JsonParser.parseJson(hurt.getData()));
            id= hurt.getId();
        }
    }
}
