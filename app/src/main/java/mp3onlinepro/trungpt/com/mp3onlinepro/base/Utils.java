package mp3onlinepro.trungpt.com.mp3onlinepro.base;

import android.content.Context;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Trung on 11/26/2015.
 */
public class Utils
{
    public static String readFromFile(String fileName, Context context)
    {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try
        {
            fIn = context.getResources().getAssets()
                    .open(fileName, Context.MODE_PRIVATE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null)
            {
                returnString.append(line);
            }
        }
        catch (Exception e)
        {
            e.getMessage();
        }
        finally
        {
            try
            {
                if (isr != null)
                {
                    isr.close();
                }
                if (fIn != null)
                {
                    fIn.close();
                }
                if (input != null)
                {
                    input.close();
                }
            }
            catch (Exception e2)
            {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }
}
