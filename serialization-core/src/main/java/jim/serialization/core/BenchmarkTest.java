package jim.serialization.core;


import jim.serialization.core.codec.FurySerializeUtil;
import jim.serialization.core.codec.NeoRedisValueCodecHelper;
import jim.serialization.core.enums.RedisCodecType;
import jim.serialization.core.model.NeoSerializerModel;
import org.openjdk.jmh.Main;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.util.concurrent.TimeUnit;

import static jim.serialization.core.RunTest.createModel;

@State(Scope.Thread)
@OutputTimeUnit(TimeUnit.SECONDS)
@CompilerControl(value = CompilerControl.Mode.INLINE)
public class BenchmarkTest {
    static NeoSerializerModel serializerModel;
    static {
        serializerModel = createModel();
        FurySerializeUtil.registerClass(NeoSerializerModel.class);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public Object benchmarkKryo() {
        byte[] tmpBytes = NeoRedisValueCodecHelper.encodeToBytes(serializerModel, RedisCodecType.KRYO);
        return NeoRedisValueCodecHelper.decodeFromBytes(tmpBytes, RedisCodecType.KRYO, NeoSerializerModel.class);
    }

    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    public Object benchmarkFury() {
        byte[] tmpBytes = NeoRedisValueCodecHelper.encodeToBytes(serializerModel, RedisCodecType.FURY);
        return NeoRedisValueCodecHelper.decodeFromBytes(tmpBytes, RedisCodecType.FURY, NeoSerializerModel.class);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(BenchmarkTest.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .warmupTime(TimeValue.seconds(2))
                .measurementIterations(10)
                .measurementTime(TimeValue.seconds(2))
                .threads(1)
                .output("/Users/jim/java/neocrm/jim-serialization-test/serialization-core/target/benchmark.log")
                .build();

        new Runner(opt).run();

//        if (args.length == 0) {
//            String commandLine =
//                    " -f 1 -wi 5 -i 10 -t 1 -w 2s -r 2s -rf csv -rff /Users/jim/java/neocrm/jim-serialization-test/serialization-core/target/benchmark.csv";
//            System.out.println(commandLine);
//            // f fork
//            // wi Number of warmup iterations
//            // i Number of measurement iterations
//            // t Number of worker threads to run with
//            // w Minimum time to spend at each warmup iteration
//            // r Minimum time to spend at each measurement iteration
//            // rf Format type for machine-readable results
//            // rff Write machine-readable results to a given file
//            args = commandLine.split(" ");
//        }
//        Main.main(args);
    }
}
