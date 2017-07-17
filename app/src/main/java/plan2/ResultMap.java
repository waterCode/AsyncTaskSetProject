package plan2;

/**
 * Created by zmc on 2017/7/17.
 */

public interface ResultMap<FromResult, ToResult> {
    ToResult[] map(FromResult[] result);
}
