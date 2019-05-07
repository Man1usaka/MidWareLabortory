import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;

public class Main {

    public static void main(String[] args) {
        try{
            ActiveXComponent dotnetCom = null;
            dotnetCom = new ActiveXComponent("Lab3YaoCom.Application");

            //用于验证的字符串
            String test = "ALSJDQIOWEUASFBNHKJABCNALBSCJBASKLCJBASIHQUWEASDKJHZXCBN";

            //压缩，返回Base64编码字符串
            Variant encode = Dispatch.call(dotnetCom,"GZipCompressString",test);

            //解压缩，返回字符串
            Variant decode = Dispatch.call(dotnetCom,"GZipDecompressString",encode);

            //查看结果
            System.out.println("Encode:  "+encode.toString());
            System.out.println("Decode:  "+decode.toString() );
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
