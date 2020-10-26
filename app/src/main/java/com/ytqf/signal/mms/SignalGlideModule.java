package com.ytqf.signal.mms;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.annotation.NonNull;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.DiskCache;
import com.bumptech.glide.load.engine.cache.DiskCacheAdapter;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.UnitModelLoader;
import com.bumptech.glide.load.resource.bitmap.Downsampler;
import com.bumptech.glide.load.resource.bitmap.StreamBitmapDecoder;
import com.bumptech.glide.load.resource.gif.ByteBufferGifDecoder;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.load.resource.gif.StreamGifDecoder;
import com.bumptech.glide.module.AppGlideModule;

import com.ytqf.signal.blurhash.BlurHash;
import com.ytqf.signal.blurhash.BlurHashModelLoader;
import com.ytqf.signal.blurhash.BlurHashResourceDecoder;
import com.ytqf.signal.contacts.avatars.ContactPhoto;
import com.ytqf.signal.crypto.AttachmentSecret;
import com.ytqf.signal.crypto.AttachmentSecretProvider;
import com.ytqf.signal.giph.model.ChunkedImageUrl;
import com.ytqf.signal.glide.cache.ApngBufferCacheDecoder;
import com.ytqf.signal.glide.cache.EncryptedApngCacheEncoder;
import com.ytqf.signal.glide.ChunkedImageUrlLoader;
import com.ytqf.signal.glide.ContactPhotoLoader;
import com.ytqf.signal.glide.cache.ApngFrameDrawableTranscoder;
import com.ytqf.signal.glide.OkHttpUrlLoader;
import com.ytqf.signal.glide.cache.ApngStreamCacheDecoder;
import com.ytqf.signal.glide.cache.EncryptedBitmapResourceEncoder;
import com.ytqf.signal.glide.cache.EncryptedCacheDecoder;
import com.ytqf.signal.glide.cache.EncryptedCacheEncoder;
import com.ytqf.signal.glide.cache.EncryptedGifDrawableResourceEncoder;
import org.signal.glide.apng.decode.APNGDecoder;
import com.ytqf.signal.mms.AttachmentStreamUriLoader.AttachmentModel;
import com.ytqf.signal.mms.DecryptableStreamUriLoader.DecryptableUri;
import com.ytqf.signal.stickers.StickerRemoteUri;
import com.ytqf.signal.stickers.StickerRemoteUriLoader;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;

@GlideModule
public class SignalGlideModule extends AppGlideModule {

  @Override
  public boolean isManifestParsingEnabled() {
    return false;
  }

  @Override
  public void applyOptions(Context context, GlideBuilder builder) {
    builder.setLogLevel(Log.ERROR);
  }

  @Override
  public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
    AttachmentSecret attachmentSecret = AttachmentSecretProvider.getInstance(context).getOrCreateAttachmentSecret();
    byte[]           secret           = attachmentSecret.getModernKey();

    registry.prepend(File.class, File.class, UnitModelLoader.Factory.getInstance());

    registry.prepend(InputStream.class, new EncryptedCacheEncoder(secret, glide.getArrayPool()));

    registry.prepend(Bitmap.class, new EncryptedBitmapResourceEncoder(secret));
    registry.prepend(File.class, Bitmap.class, new EncryptedCacheDecoder<>(secret, new StreamBitmapDecoder(new Downsampler(registry.getImageHeaderParsers(), context.getResources().getDisplayMetrics(), glide.getBitmapPool(), glide.getArrayPool()), glide.getArrayPool())));

    registry.prepend(GifDrawable.class, new EncryptedGifDrawableResourceEncoder(secret));
    registry.prepend(File.class, GifDrawable.class, new EncryptedCacheDecoder<>(secret, new StreamGifDecoder(registry.getImageHeaderParsers(), new ByteBufferGifDecoder(context, registry.getImageHeaderParsers(), glide.getBitmapPool(), glide.getArrayPool()), glide.getArrayPool())));

    ApngBufferCacheDecoder apngBufferCacheDecoder = new ApngBufferCacheDecoder();
    ApngStreamCacheDecoder apngStreamCacheDecoder = new ApngStreamCacheDecoder(apngBufferCacheDecoder);

    registry.prepend(InputStream.class, APNGDecoder.class, apngStreamCacheDecoder);
    registry.prepend(ByteBuffer.class, APNGDecoder.class, apngBufferCacheDecoder);
    registry.prepend(APNGDecoder.class, new EncryptedApngCacheEncoder(secret));
    registry.prepend(File.class, APNGDecoder.class, new EncryptedCacheDecoder<>(secret, apngStreamCacheDecoder));
    registry.register(APNGDecoder.class, Drawable.class, new ApngFrameDrawableTranscoder());

    registry.prepend(BlurHash.class, Bitmap.class, new BlurHashResourceDecoder());

    registry.append(ContactPhoto.class, InputStream.class, new ContactPhotoLoader.Factory(context));
    registry.append(DecryptableUri.class, InputStream.class, new DecryptableStreamUriLoader.Factory(context));
    registry.append(AttachmentModel.class, InputStream.class, new AttachmentStreamUriLoader.Factory());
    registry.append(ChunkedImageUrl.class, InputStream.class, new ChunkedImageUrlLoader.Factory());
    registry.append(StickerRemoteUri.class, InputStream.class, new StickerRemoteUriLoader.Factory());
    registry.append(BlurHash.class, BlurHash.class, new BlurHashModelLoader.Factory());
    registry.replace(GlideUrl.class, InputStream.class, new OkHttpUrlLoader.Factory());
  }

  public static class NoopDiskCacheFactory implements DiskCache.Factory {
    @Override
    public DiskCache build() {
      return new DiskCacheAdapter();
    }
  }
}
