package sun.target.img.glide;


import androidx.annotation.NonNull;

import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.data.DataFetcher;

import java.io.InputStream;

/**
 * created by sfx on 2017/12/28.
 */

public class MultiBitmapFetcher implements DataFetcher<InputStream> {

    private final MultiBitmapUrl url;
    private final RequestManager requestManager;

    MultiBitmapFetcher(MultiBitmapUrl faceUrlSet, RequestManager requestManager) {
        this.url = faceUrlSet;
        this.requestManager = requestManager;
    }

    @Override
    public void loadData(Priority priority, DataCallback<? super InputStream> callback) {
        try {
            url.loadFaceInputStream(requestManager, callback);
        } catch (Exception e) {
            callback.onLoadFailed(e);
        }
    }

    @Override
    public void cleanup() {
        if (url != null) {
            url.cleanup();
        }
    }

    @Override
    public void cancel() {
        if (url != null) {
            url.cleanup();
        }
    }

    @NonNull
    @Override
    public Class<InputStream> getDataClass() {
        return InputStream.class;
    }

    @NonNull
    @Override
    public DataSource getDataSource() {
        return DataSource.REMOTE;
    }
}
