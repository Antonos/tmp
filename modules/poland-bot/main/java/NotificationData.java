import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by anton on 02.02.2015.
 */
public class NotificationData implements Serializable {
    public HashMap<String, Date> getValues() {
        return values;
    }

    {values = new HashMap<String, Date>();}

    public void setValues(HashMap<String, Date> values) {
        this.values = values;
    }

    private HashMap<String,Date> values;
}
