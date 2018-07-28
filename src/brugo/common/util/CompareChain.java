package brugo.common.util;

public class CompareChain
{
    protected int result;

    public CompareChain()
    {
    }

    public CompareChain(int result)
    {
        this.result = result;
    }

    public CompareChain compareTo(int obj1, int obj2)
    {
        if (result == 0) result = CompareUtil.compareTo(obj1, obj2);
        return this;
    }

    public <T> CompareChain compareTo(Comparable<T> obj1, T obj2)
    {
        if (result == 0) result = CompareUtil.compareTo(obj1, obj2);
        return this;
    }

    public <T> CompareChain compareTo(Comparable<T>[] a1, T[] a2)
    {
        if (result == 0) result = CompareUtil.compareTo(a1, a2);
        return this;
    }

    public <T> CompareChain compareTo(Comparable<T>[][] a1, T[][] a2)
    {
        if (result == 0) result = CompareUtil.compareTo(a1, a2);
        return this;
    }

    public int getResult() {
        return result;
    }
}
