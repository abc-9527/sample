package sun.target.img.glide;

import android.content.Context;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;

import java.io.InputStream;

/**
 * created by sfx on 2017/12/28.
 */

public class MultiBitmapLoader implements ModelLoader<MultiBitmapUrl, InputStream> {

    private final Context context;

    private MultiBitmapLoader(Context context) {
        this.context = context;
    }

    @Nullable
    @Override
    public LoadData<InputStream> buildLoadData(MultiBitmapUrl model, int width, int height, Options options) {
        return new LoadData<>(model, new MultiBitmapFetcher(model, Glide.with(context)));
    }

    @Override
    public boolean handles(MultiBitmapUrl FaceUrlSet) {
        return true;
    }

    /**
     * The default factory for {@link MultiBitmapLoader}s.
     */
    public static class Factory implements ModelLoaderFactory<MultiBitmapUrl, InputStream> {
        private final Context context;

        public Factory(Context context) {
            this.context = context.getApplicationContext();
        }

        @NonNull
        @Override
        public ModelLoader<MultiBitmapUrl, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new MultiBitmapLoader(context);
        }

        @Override
        public void teardown() {
            // Do nothing.
        }
    }
}
