package mp3onlinepro.trungpt.com.mp3onlinepro;

import org.greenrobot.greendao.generator.DaoGenerator;
import org.greenrobot.greendao.generator.Entity;
import org.greenrobot.greendao.generator.Property;
import org.greenrobot.greendao.generator.Schema;

public class MyClass
{
    private static final String PROJECT_DIR = System.getProperty("user.dir");

    public static void main(String[] args)
    {
        Schema schema = new Schema(1, "com.cntt.freemusicdownloadnow.db");
        schema.enableKeepSectionsByDefault();

        addTables(schema);

        try
        {
            new DaoGenerator().generateAll(schema, PROJECT_DIR + "/app/src/main/java");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static void addTables(final Schema schema)
    {
        Entity song = addSong(schema);
    }

    private static Entity addSong(final Schema schema)
    {
        Entity song = schema.addEntity("FavoriteSong");
        song.addIdProperty().primaryKey();
        song.addStringProperty("title");
        song.addStringProperty("urlSource");
        song.addStringProperty("urlThumbnail");
        song.addStringProperty("urlDownload");
        song.addStringProperty("artist");
        song.addLongProperty("duration");
        return song;
    }

}
