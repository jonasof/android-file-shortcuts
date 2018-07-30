package jonasof.fileshortcuts;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.Toast;

import com.obsez.android.lib.filechooser.ChooserDialog;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.this.openFile(1);
            }
        });
    }

    public void addShortcut(Uri uri, String title) {
        Context context = this.getApplicationContext();
        ShortcutManager shortcutManager = context.getSystemService(ShortcutManager.class);

        if (!shortcutManager.isRequestPinShortcutSupported()) {
            Toast.makeText(context, "Device doesn't support shortcuts (not Android > 8.0?)", Toast.LENGTH_SHORT).show();
            return;
        }

        ShortcutInfo pinShortcutInfo = createShortcutInfo(this, uri, title);
        Intent resultIntent = shortcutManager.createShortcutResultIntent(pinShortcutInfo);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, resultIntent, 0);
        shortcutManager.requestPinShortcut(pinShortcutInfo, pendingIntent.getIntentSender());
    }

    private ShortcutInfo createShortcutInfo(Context context, Uri uri, String title)
    {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, getMimeType(uri.getPath()));

        return new ShortcutInfo.Builder(context, uri.getPath())
            .setShortLabel(title)
            // .setLongLabel("ESTOU TESTANDO")
            .setIntent(intent)
            .build();
    }

    private void openFile(int CODE)
    {
        new ChooserDialog().with(this)
            .withStartFile(Environment.getExternalStorageDirectory().getAbsolutePath())
            .withChosenListener(new ChooserDialog.Result() {
                @Override
                public void onChoosePath(String path, File pathFile) {

                    final Uri uri = Uri.fromFile(pathFile);

                    AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);

                    alert.setTitle("Digite o nome do arquivo");
                    alert.setMessage("Aparecerá abaixo do ícone");

                    final EditText input = new EditText(MainActivity.this);
                    alert.setView(input);

                    alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            MainActivity.this.addShortcut(uri, input.getText().toString());
                        }
                    });

                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                        }
                    });

                    alert.show();
                }
            })
            .build()
            .show();
    }

    public static String getMimeType(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }
}
