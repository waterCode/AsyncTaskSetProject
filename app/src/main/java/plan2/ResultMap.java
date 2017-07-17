package plan2;

/**
 * Created by zmc on 2017/7/17.
 */

/**
 *
 * @param <FromResult> 需要转换数据的原类型
 * @param <ToResult> 需要转换数据转换后的数据类型
 */
public interface ResultMap<FromResult, ToResult> {
    ToResult[] map(FromResult[] result);
}
