package pl.kalksztejn.hurt_ai.ui.home;

import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import pl.kalksztejn.hurt_ai.parser.JsonParser;
import pl.kalksztejn.hurt_ai.service.DetectionService;
import pl.kalksztejn.hurt_ai.service.FirebaseHurtService;
import pl.kalksztejn.hurt_ai.utils.ImageUtils;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<Drawable> imageDrawable;
    private View.OnClickListener uploadClickListener;

    public HomeViewModel() {
        imageDrawable = new MutableLiveData<>();
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }

    public LiveData<Drawable> getImageDrawable() {
        return imageDrawable;
    }

    public void setImageDrawable(Drawable drawable) {
        imageDrawable.setValue(drawable);
    }

    public View.OnClickListener getUploadClickListener() {
        DetectionService rest = new DetectionService();
        Drawable image = imageDrawable.getValue();
        if (image != null) {
            rest.sendImage(image, new DetectionService.ApiResponseCallback() {
                @Override
                public void onSuccess(String response) {
                    mText.postValue(JsonParser.parseJson(response));
                    FirebaseHurtService service = new FirebaseHurtService();
                    service.saveRecord(ImageUtils.drawableToBase64(image), response);
                }

                @Override
                public void onFailure(String errorMessage) {
                    mText.postValue(errorMessage);
                }
            });
        } else {
            System.out.println("Image is null.");
        }
        return uploadClickListener;
    }

    public void setUploadClickListener(View.OnClickListener listener) {
        uploadClickListener = listener;
    }
}