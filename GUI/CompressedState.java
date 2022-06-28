public class CompressedState implements Comparable<CompressedState>{
    public byte[] compressed;
    public CompressedState(){}
    public CompressedState(byte[] compressed){
        this.compressed = compressed;
    }
    public int compareTo(CompressedState c) {
        if(compressed.length != c.compressed.length)
            return compressed.length > c.compressed.length ? 1 : -1;
        if(compressed.length == c.compressed.length)
            return 0;
        for(int i = 0; i < compressed.length; i++)
            if(compressed[i] != c.compressed[i])
                return compressed[i] > c.compressed[i] ? 1 : -1;
        return 0;
    }
}
