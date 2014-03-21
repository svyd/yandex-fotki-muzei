package com.blogspot.vsvydenko.yafotki_muzei;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Toast;

import com.google.android.apps.muzei.api.Artwork;
import com.google.android.apps.muzei.api.RemoteMuzeiArtSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;

import com.blogspot.vsvydenko.yafotki_muzei.YandexFotkiServiceInterface.PhotosResponse;
import com.blogspot.vsvydenko.yafotki_muzei.YandexFotkiServiceInterface.Photo;
import com.google.android.apps.muzei.api.UserCommand;

/**
 * Created by vsvydenko on 26.02.14.
 */
public class YandexFotkiArtSource extends RemoteMuzeiArtSource {

    public static String ACTION_UPDATE  = "ACTION_UPDATE";
    public static String SOURCE_NAME    = "YandexFotkiArtSource";
    public static String POPULAR        = "POPULAR";
    public static String POD            = "POD";

    private static final int COMMAND_ID_SHARE = 1;

    PhotosResponse response;

    public YandexFotkiArtSource() {
        super(SOURCE_NAME);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        List<UserCommand> commands = new ArrayList<UserCommand>();
        commands.add(new UserCommand(BUILTIN_COMMAND_ID_NEXT_ARTWORK));
        commands.add(new UserCommand(COMMAND_ID_SHARE, getString(R.string.action_share_artwork)));
        setUserCommands(commands);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            super.onHandleIntent(intent);
            return;
        }

        String action = intent.getAction();
        if (ACTION_UPDATE.equals(action)) {
            scheduleUpdate(System.currentTimeMillis() + 1000);
        }

        super.onHandleIntent(intent);
    }

    @Override
    protected void onTryUpdate(int reason) throws RetryException {

        if (PreferenceHelper.isWiFiChecked() && !Utils.isWiFiOn(this)) {
            return;
        }

        String currentToken = (getCurrentArtwork() != null) ? getCurrentArtwork().getToken() : null;

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setServer("http://api-fotki.yandex.ru/api")
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError cause) {
                        if (cause.getResponse() == null)
                            return new RetryException();
                        scheduleUpdate(System.currentTimeMillis() + PreferenceHelper.getInterval());
                        return cause;
                    }
                })
                .build();

        YandexFotkiServiceInterface yandexFotkiService = restAdapter.
                create(YandexFotkiServiceInterface.class);

        if (PreferenceHelper.getSourceUrl().equals(POD)) {
            response = yandexFotkiService.getPODPhoto(Utils.getDate());
        } else {
            response = yandexFotkiService.getTopPhotos();
        }


        if (response == null || response.entries == null) {
            throw new RetryException();
        }

        if (response.entries.isEmpty()) {
            scheduleUpdate(System.currentTimeMillis() + PreferenceHelper.getInterval());
            return;
        }

        Random random = new Random();
        Photo photo;
        String token;

        while (true) {
            photo = response.entries.get(random.nextInt(response.entries.size()));
            token = photo.id;
            if (response.entries.size() <= 1 || !TextUtils.equals(token, currentToken)) {
                break;
            }
        }

        publishArtwork(new Artwork.Builder()
                .title(photo.title)
                .byline(photo.author)
                .imageUri(Uri.parse(photo.img.XXXL.href))
                .token(token)
                .viewIntent(new Intent(Intent.ACTION_VIEW,
                        Uri.parse(photo.links.alternate)))
                .build()
        );

        scheduleUpdate(System.currentTimeMillis() + PreferenceHelper.getInterval());
    }

    @Override
    protected void onCustomCommand(int id) {
        super.onCustomCommand(id);
        if (COMMAND_ID_SHARE == id) {
            Artwork currentArtwork = getCurrentArtwork();
            if (currentArtwork == null) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(YandexFotkiArtSource.this,
                                R.string.yandexfotki_source_error_no_artwork_to_share,
                                Toast.LENGTH_SHORT).show();
                    }
                });
                return;
            }

            String detailUrl = (currentArtwork.getViewIntent().getDataString());
            String artist = currentArtwork.getByline()
                    .replaceFirst("\\.\\s*($|\\n).*", "").trim();

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "My Android wallpaper today is '"
                    + currentArtwork.getTitle().trim()
                    + "' by " + artist
                    + ". #Muzei #YandexFotki\n\n"
                    + detailUrl);
            shareIntent = Intent.createChooser(shareIntent, "Share artwork");
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(shareIntent);

        }
    }
}
