package jim.serialization.core;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.ReflectUtil;
import com.google.common.base.Stopwatch;
import jim.serialization.core.codec.FurySerializeUtil;
import jim.serialization.core.codec.NeoRedisValueCodecHelper;
import jim.serialization.core.enums.RedisCodecType;
import jim.serialization.core.model.NeoSerializerModel;

import java.util.concurrent.TimeUnit;

public class RunTest {
    public static void main(String[] args) {
        NeoSerializerModel serializerModel=createModel();
        FurySerializeUtil.registerClass(NeoSerializerModel.class);
        int testCount=1000;

        byte[] kryoBytes= NeoRedisValueCodecHelper.encodeToBytes(serializerModel, RedisCodecType.KRYO);
        byte[] furyBytes= NeoRedisValueCodecHelper.encodeToBytes(serializerModel, RedisCodecType.FURY);
        Stopwatch stopwatch= Stopwatch.createUnstarted();

        stopwatch.start();
        for(int i=0;i<testCount;i++) {
            byte[] tmpBytes= NeoRedisValueCodecHelper.encodeToBytes(serializerModel, RedisCodecType.KRYO);
            NeoSerializerModel tmpModel= NeoRedisValueCodecHelper.decodeFromBytes(tmpBytes, RedisCodecType.KRYO, NeoSerializerModel.class);
            Assert.isTrue(null!=tmpModel, "decode error");
        }
        stopwatch.stop();
        long kryoAvgTime=stopwatch.elapsed(TimeUnit.MICROSECONDS)/testCount;

        //warmup
        int warmupTestCount=100000;
        for(int i=0;i<warmupTestCount;i++){
            byte[] tmpBytes= NeoRedisValueCodecHelper.encodeToBytes(serializerModel, RedisCodecType.FURY);
            NeoSerializerModel tmpModel= NeoRedisValueCodecHelper.decodeFromBytes(tmpBytes, RedisCodecType.FURY, NeoSerializerModel.class);
        }

        stopwatch.start();
        for(int i=0;i<testCount;i++) {
            byte[] tmpBytes= NeoRedisValueCodecHelper.encodeToBytes(serializerModel, RedisCodecType.FURY);
            NeoSerializerModel tmpModel= NeoRedisValueCodecHelper.decodeFromBytes(tmpBytes, RedisCodecType.FURY, NeoSerializerModel.class);
            Assert.isTrue(null!=tmpModel, "decode error");
        }
        stopwatch.stop();
        long furyAvgTime=stopwatch.elapsed(TimeUnit.MICROSECONDS)/testCount;

        String result=
                "  kryoBytes.length="+kryoBytes.length
                +" furyBytes.length="+furyBytes.length
                +"\n"
                +" kryoAvgTime="+kryoAvgTime
                +" furyAvgTime="+furyAvgTime;
        System.out.println(result);
                ;
    }
    public static NeoSerializerModel createModel(){
        NeoSerializerModel neoSerializerModel=new NeoSerializerModel();
        int doubleCount=100;
        for(int i=1;i<=doubleCount;i++) {
            if(i%2==0) {
                String fieldName="double"+i;
                ReflectUtil.setFieldValue(neoSerializerModel,fieldName,i);
            }
        }
        neoSerializerModel.setId3(3l);
        int stringCount=200;
        for(int i=1;i<=stringCount;i++) {
            if(i%5==0) {
                String fieldName="userName"+i;
                ReflectUtil.setFieldValue(neoSerializerModel,fieldName,"abcdesdflkjlkjlkj"+i);
            }
        }
        int integerCount=100;
        for(int i=1;i<=integerCount;i++) {
            if(i%3==0) {
                String fieldName="integer"+i;
                ReflectUtil.setFieldValue(neoSerializerModel,fieldName,i);
            }
        }
        return neoSerializerModel;
    }
}
