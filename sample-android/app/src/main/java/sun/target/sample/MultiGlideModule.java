package sun.target.sample;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.module.AppGlideModule;


import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import sun.target.img.glide.MultiBitmapLoader;
import sun.target.img.glide.MultiBitmapUrl;


@GlideModule
public class MultiGlideModule extends AppGlideModule {
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Apply options to the builder here.
    }

    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {
        //创建缓存对象
        OkHttpClient.Builder client = new OkHttpClient.Builder()
                .connectTimeout(45, TimeUnit.SECONDS)
                .readTimeout(45, TimeUnit.SECONDS)
                .writeTimeout(45, TimeUnit.SECONDS)
                .addNetworkInterceptor(new CacheInterceptor());
        registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory(client.build()));
        registry.append(MultiBitmapUrl.class, InputStream.class, new MultiBitmapLoader.Factory(context));

    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }

    static class CacheInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Response originResponse = chain.proceed(chain.request());
            //设置缓存时间为，并移除了pragma消息头，移除它的原因是因为pragma也是控制缓存的一个消息头属性
            return originResponse.newBuilder().removeHeader("pragma")
                    .header("Cache-Control", "max-age=10")//设置10秒
                    .header("Cache-Control", "max-stale=30").build();
        }
    }
}