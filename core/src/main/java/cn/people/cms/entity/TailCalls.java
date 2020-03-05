package cn.people.cms.entity;

/**
 * @author : 张新征
 * Date: 2017/9/13 16:10
 * Description:
 */
public class TailCalls {
    public static <T> TailCall<T> call(final TailCall<T> nextCall) {
        return nextCall;
    }
    public static <T> TailCall<T> done(final T value) {
        return new TailCall<T>() {
            @Override public boolean isComplete() { return true; }
            @Override public T result() { return value; }
            @Override public TailCall<T> apply() {
                throw new Error("end of recursion");
            }
        };
    }
}
