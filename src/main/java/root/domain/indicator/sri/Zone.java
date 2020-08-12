package root.domain.indicator.sri;

import lombok.Value;
import org.ta4j.core.num.Num;

import java.util.List;

@Value
public class Zone
{
    Type type;
    Num height;
    List<Num> values;

    public int getStrength()
    {
        return values.size();
    }

    public enum Type
    {
        SUPPORT, RESISTANCE;
    }
}
