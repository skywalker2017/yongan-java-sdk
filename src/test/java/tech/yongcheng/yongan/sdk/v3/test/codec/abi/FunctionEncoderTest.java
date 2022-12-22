package tech.yongcheng.yongan.sdk.v3.test.codec.abi;

import tech.yongcheng.yongan.sdk.v3.test.codec.TestFixture;
import tech.yongcheng.yongan.sdk.v3.test.codec.TestUtils;
import tech.yongcheng.yongan.sdk.v3.codec.abi.FunctionEncoder;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.generated.Bytes10;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.generated.StaticArray3;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.generated.Uint256;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.generated.Uint32;
import tech.yongcheng.yongan.sdk.v3.crypto.CryptoSuite;
import org.junit.Assert;
import org.junit.Test;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.*;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class FunctionEncoderTest {
    private final FunctionEncoder ecdsaEncoder = new FunctionEncoder(new CryptoSuite(0));
    private final FunctionEncoder smEncoder = new FunctionEncoder(new CryptoSuite(1));

    @Test
    public void testBuildMethodId() {
        byte[] methodId = ecdsaEncoder.buildMethodId("baz(uint32,bool)");
        Assert.assertEquals("cdcd77c0", TestUtils.bytesToString(methodId));

        methodId = smEncoder.buildMethodId("baz(uint32,bool)");
        assertEquals("638408c2", TestUtils.bytesToString(methodId));
    }

    @Test
    public void testBuildEmptyMethodSignature() {
        Assert.assertEquals(
                "empty()",
                FunctionEncoder.buildMethodSignature("empty", Collections.emptyList()));
    }

    @Test
    public void testEncodeConstructorEmpty() {
        assertEquals(FunctionEncoder.encodeConstructor(Collections.emptyList()).length, 0);
    }

    @Test
    public void testEncodeConstructorString() {
        assertEquals(
                "0000000000000000000000000000000000000000000000000000000000000020"
                        + "000000000000000000000000000000000000000000000000000000000000000a"
                        + "4772656574696e67732100000000000000000000000000000000000000000000",
                TestUtils.bytesToString(FunctionEncoder.encodeConstructor(
                        Collections.singletonList(new Utf8String("Greetings!")))));
    }

    @Test
    public void testEncodeConstructorUint() {
        assertEquals(
                "0000000000000000000000000000000000000000000000000000000000000001"
                        + "0000000000000000000000000000000000000000000000000000000000000020",
                TestUtils.bytesToString(FunctionEncoder.encodeConstructor(
                        Arrays.asList(
                                new Uint(BigInteger.ONE), new Uint(BigInteger.valueOf(0x20))))));
    }

    @Test
    public void testFunctionSimpleEncode() {
        Function function =
                new Function(
                        "baz",
                        Arrays.asList(new Uint32(BigInteger.valueOf(69)), new Bool(true)),
                        Collections.emptyList());

        assertEquals(
                "cdcd77c0"
                        + "0000000000000000000000000000000000000000000000000000000000000045"
                        + "0000000000000000000000000000000000000000000000000000000000000001",
                TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }

    @Test
    public void testFunctionMDynamicArrayEncode1() {
        Function function =
                new Function(
                        "sam",
                        Arrays.asList(
                                new DynamicBytes("dave".getBytes()),
                                new Bool(true),
                                new DynamicArray<>(
                                        new Uint(BigInteger.ONE),
                                        new Uint(BigInteger.valueOf(2)),
                                        new Uint(BigInteger.valueOf(3)))),
                        Collections.emptyList());

        assertEquals(
                "a5643bf2"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "00000000000000000000000000000000000000000000000000000000000000a0"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6461766500000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000003"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "0000000000000000000000000000000000000000000000000000000000000003",
                TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }

    @Test
    public void testFunctionMDynamicArrayEncode2() {
        Function function =
                new Function(
                        "f",
                        Arrays.asList(
                                new Uint(BigInteger.valueOf(0x123)),
                                new DynamicArray<>(
                                        new Uint32(BigInteger.valueOf(0x456)),
                                        new Uint32(BigInteger.valueOf(0x789))),
                                new Bytes10("1234567890".getBytes()),
                                new DynamicBytes("Hello, world!".getBytes())),
                        Collections.emptyList());

        assertEquals(
                "8be65246"
                        + "0000000000000000000000000000000000000000000000000000000000000123"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "3132333435363738393000000000000000000000000000000000000000000000"
                        + "00000000000000000000000000000000000000000000000000000000000000e0"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "0000000000000000000000000000000000000000000000000000000000000456"
                        + "0000000000000000000000000000000000000000000000000000000000000789"
                        + "000000000000000000000000000000000000000000000000000000000000000d"
                        + "48656c6c6f2c20776f726c642100000000000000000000000000000000000000",
                TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }

    @Test
    public void testStaticStructEncode() {
        Function function =
                new Function(
                        "setBar",
                        Arrays.<Type>asList(new TestFixture.Bar(BigInteger.ONE, BigInteger.TEN)),
                        Collections.<TypeReference<?>>emptyList());

        assertEquals(
                "3d761de6"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "000000000000000000000000000000000000000000000000000000000000000a",
                TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }

    @Test
    public void testDynamicStructEncode() {
        Function function =
                new Function(
                        "setFoo",
                        Arrays.<Type>asList(new TestFixture.Foo("id", "name")),
                        Collections.emptyList());
        assertEquals(
                "2cf07395"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000",
                TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }

    @Test
    public void testDynamicStructWithStaticFieldEncode() {
        Function function =
                new Function(
                        "setBaz",
                        Arrays.<Type>asList(new TestFixture.Baz("id", BigInteger.ONE)),
                        Collections.<TypeReference<?>>emptyList());
        assertEquals(
                "9096c213"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000",
                TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }

    @Test
    public void testDynamicStructWithStaticFieldEncode2() {
        Function function =
                new Function(
                        "setBoz",
                        Arrays.<Type>asList(new TestFixture.Boz(BigInteger.ONE, "id")),
                        Collections.<TypeReference<?>>emptyList());

        assertEquals(
                "be9c5e34"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000",
                TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }

    @Test
    public void testStaticStructNestedEncode() {
        Function function =
                new Function(
                        "setFuzz",
                        Arrays.<Type>asList(
                                new TestFixture.Fuzz(
                                        new TestFixture.Bar(BigInteger.ONE, BigInteger.TEN),
                                        BigInteger.ONE)),
                        Collections.emptyList());
        assertEquals(
                "ad204a12"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "000000000000000000000000000000000000000000000000000000000000000a"
                        + "0000000000000000000000000000000000000000000000000000000000000001",
                TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }

    @Test
    public void testDynamicStructNestedEncode() {
        Function function =
                new Function(
                        "setNuu",
                        Arrays.<Type>asList(new TestFixture.Nuu(new TestFixture.Foo("id", "name"))),
                        Collections.<TypeReference<?>>emptyList());
        assertEquals(
                "8c9fb6f9"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000",
                TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }

    @Test
    public void testDynamicStructNestedEncode2() {
        Function function =
                new Function(
                        "setNaz",
                        Arrays.<Type>asList(
                                new TestFixture.Naz(
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("id", "name"))), BigInteger.ONE)),
                        Collections.emptyList());
        assertEquals(
                "6bb632a9"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000",
                TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }

    @Test
    public void testMultiReturnStaticDynamicArrayWithStaticDynamicStructs() {
        Function function =
                new Function(
                        "idNarBarFooNarFooArrays",
                        Arrays.asList(
                                new StaticArray3<>(
                                        TestFixture.Nar.class,
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("4", "nestedFoo"))),
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("", ""))),
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("4", "nestedFoo")))),
                                new StaticArray3<>(
                                        TestFixture.Bar.class,
                                        new TestFixture.Bar(BigInteger.ZERO, BigInteger.ZERO),
                                        new TestFixture.Bar(
                                                BigInteger.valueOf(123), BigInteger.valueOf(123)),
                                        new TestFixture.Bar(BigInteger.ZERO, BigInteger.ZERO)),
                                new DynamicArray<>(
                                        TestFixture.Foo.class,
                                        new TestFixture.Foo("id", "name")),
                                new DynamicArray<>(
                                        TestFixture.Nar.class,
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("4", "nestedFoo"))),
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("4", "nestedFoo"))),
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("", "")))),
                                new StaticArray3<>(
                                        TestFixture.Foo.class,
                                        new TestFixture.Foo("id", "name"),
                                        new TestFixture.Foo("id", "name"),
                                        new TestFixture.Foo("id", "name"))),
                        Arrays.asList(
                                new TypeReference<StaticArray3<TestFixture.Nar>>() {
                                },
                                new TypeReference<StaticArray3<TestFixture.Bar>>() {
                                },
                                new TypeReference<DynamicArray<TestFixture.Foo>>() {
                                },
                                new TypeReference<DynamicArray<TestFixture.Nar>>() {
                                },
                                new TypeReference<StaticArray3<TestFixture.Foo>>() {
                                }));
        String encodedInput =
                "0f8676d2"
                        + "0000000000000000000000000000000000000000000000000000000000000140"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "000000000000000000000000000000000000000000000000000000000000007b"
                        + "000000000000000000000000000000000000000000000000000000000000007b"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000460"
                        + "0000000000000000000000000000000000000000000000000000000000000560"
                        + "00000000000000000000000000000000000000000000000000000000000008a0"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000160"
                        + "0000000000000000000000000000000000000000000000000000000000000220"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "3400000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000009"
                        + "6e6573746564466f6f0000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "3400000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000009"
                        + "6e6573746564466f6f0000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000003"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000160"
                        + "0000000000000000000000000000000000000000000000000000000000000260"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "3400000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000009"
                        + "6e6573746564466f6f0000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "3400000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000009"
                        + "6e6573746564466f6f0000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000120"
                        + "00000000000000000000000000000000000000000000000000000000000001e0"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000";
        assertEquals(
                encodedInput,
                TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }

    @Test
    public void testEncodeStructMultipleDynamicStaticArray() {
        Function function =
                new Function(
                        "idNarBarFooNarFooArrays",
                        Arrays.<Type>asList(
                                new DynamicArray<>(
                                        TestFixture.Nar.class,
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("4", "nestedFoo"))),
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("4", "nestedFoo"))),
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("", "")))),
                                new StaticArray3<>(
                                        TestFixture.Bar.class,
                                        new TestFixture.Bar(BigInteger.ZERO, BigInteger.ZERO),
                                        new TestFixture.Bar(
                                                BigInteger.valueOf(123), BigInteger.valueOf(123)),
                                        new TestFixture.Bar(BigInteger.ZERO, BigInteger.ZERO)),
                                new DynamicArray<>(
                                        TestFixture.Foo.class,
                                        new TestFixture.Foo("id", "name")),
                                new DynamicArray<>(
                                        TestFixture.Nar.class,
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("4", "nestedFoo"))),
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("4", "nestedFoo"))),
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("", "")))),
                                new DynamicArray<>(
                                        TestFixture.Foo.class,
                                        new TestFixture.Foo("id", "name"))),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<DynamicArray<TestFixture.Nar>>() {
                                },
                                new TypeReference<StaticArray3<TestFixture.Bar>>() {
                                },
                                new TypeReference<DynamicArray<TestFixture.Foo>>() {
                                },
                                new TypeReference<DynamicArray<TestFixture.Nar>>() {
                                },
                                new TypeReference<DynamicArray<TestFixture.Foo>>() {
                                }));
        String expected =
                "7fcdab08"
                        + "0000000000000000000000000000000000000000000000000000000000000140"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "000000000000000000000000000000000000000000000000000000000000007b"
                        + "000000000000000000000000000000000000000000000000000000000000007b"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000480"
                        + "0000000000000000000000000000000000000000000000000000000000000580"
                        + "00000000000000000000000000000000000000000000000000000000000008c0"
                        + "0000000000000000000000000000000000000000000000000000000000000003"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000160"
                        + "0000000000000000000000000000000000000000000000000000000000000260"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "3400000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000009"
                        + "6e6573746564466f6f0000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "3400000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000009"
                        + "6e6573746564466f6f0000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000003"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000160"
                        + "0000000000000000000000000000000000000000000000000000000000000260"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "3400000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000009"
                        + "6e6573746564466f6f0000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "3400000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000009"
                        + "6e6573746564466f6f0000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000";

        assertEquals(
                expected, TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }

    @Test
    public void testEncodeStructMultipleDynamicStaticArray2() {
        Function function =
                new Function(
                        "idBarNarFooNarFooArrays",
                        Arrays.<Type>asList(
                                new StaticArray3<>(
                                        TestFixture.Bar.class,
                                        new TestFixture.Bar(BigInteger.ZERO, BigInteger.ZERO),
                                        new TestFixture.Bar(
                                                BigInteger.valueOf(123), BigInteger.valueOf(123)),
                                        new TestFixture.Bar(BigInteger.ZERO, BigInteger.ZERO)),
                                new StaticArray3<>(
                                        TestFixture.Nar.class,
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("4", "nestedFoo"))),
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("", ""))),
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("4", "nestedFoo")))),
                                new DynamicArray<>(
                                        TestFixture.Foo.class,
                                        new TestFixture.Foo("id", "name")),
                                new DynamicArray<>(
                                        TestFixture.Nar.class,
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("4", "nestedFoo"))),
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("4", "nestedFoo"))),
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("", "")))),
                                new StaticArray3<>(
                                        TestFixture.Foo.class,
                                        new TestFixture.Foo("id", "name"),
                                        new TestFixture.Foo("id", "name"),
                                        new TestFixture.Foo("id", "name"))),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<StaticArray3<TestFixture.Bar>>() {
                                },
                                new TypeReference<StaticArray3<TestFixture.Nar>>() {
                                },
                                new TypeReference<DynamicArray<TestFixture.Foo>>() {
                                },
                                new TypeReference<DynamicArray<TestFixture.Nar>>() {
                                },
                                new TypeReference<StaticArray3<TestFixture.Foo>>() {
                                }));
        String expected =
                "8a62c214"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "000000000000000000000000000000000000000000000000000000000000007b"
                        + "000000000000000000000000000000000000000000000000000000000000007b"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000140"
                        + "0000000000000000000000000000000000000000000000000000000000000460"
                        + "0000000000000000000000000000000000000000000000000000000000000560"
                        + "00000000000000000000000000000000000000000000000000000000000008a0"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000160"
                        + "0000000000000000000000000000000000000000000000000000000000000220"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "3400000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000009"
                        + "6e6573746564466f6f0000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "3400000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000009"
                        + "6e6573746564466f6f0000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000003"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000160"
                        + "0000000000000000000000000000000000000000000000000000000000000260"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "3400000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000009"
                        + "6e6573746564466f6f0000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "3400000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000009"
                        + "6e6573746564466f6f0000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000120"
                        + "00000000000000000000000000000000000000000000000000000000000001e0"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000";

        assertEquals(
                expected, TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }

    @Test
    public void testEncodeStructMultipleDynamicStaticArray3() {
        Function function =
                new Function(
                        "idNarBarFooNarFooArrays",
                        Arrays.<Type>asList(
                                new StaticArray3<>(
                                        TestFixture.Nar.class,
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("4", "nestedFoo"))),
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("", ""))),
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("4", "nestedFoo")))),
                                new DynamicArray<>(
                                        TestFixture.Bar.class,
                                        new TestFixture.Bar(
                                                BigInteger.valueOf(123), BigInteger.valueOf(123)),
                                        new TestFixture.Bar(
                                                BigInteger.valueOf(12), BigInteger.valueOf(33)),
                                        new TestFixture.Bar(BigInteger.ZERO, BigInteger.ZERO)),
                                new DynamicArray<>(
                                        TestFixture.Foo.class,
                                        new TestFixture.Foo("id", "name")),
                                new DynamicArray<>(
                                        TestFixture.Nar.class,
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("4", "nestedFoo"))),
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("4", "nestedFoo"))),
                                        new TestFixture.Nar(
                                                new TestFixture.Nuu(
                                                        new TestFixture.Foo("", "")))),
                                new StaticArray3<>(
                                        TestFixture.Foo.class,
                                        new TestFixture.Foo("id", "name"),
                                        new TestFixture.Foo("id", "name"),
                                        new TestFixture.Foo("id", "name"))),
                        Arrays.<TypeReference<?>>asList(
                                new TypeReference<StaticArray3<TestFixture.Nar>>() {
                                },
                                new TypeReference<DynamicArray<TestFixture.Bar>>() {
                                },
                                new TypeReference<DynamicArray<TestFixture.Foo>>() {
                                },
                                new TypeReference<DynamicArray<TestFixture.Nar>>() {
                                },
                                new TypeReference<StaticArray3<TestFixture.Foo>>() {
                                }));
        String expected =
                "1b9faea1"
                        + "00000000000000000000000000000000000000000000000000000000000000a0"
                        + "00000000000000000000000000000000000000000000000000000000000003c0"
                        + "00000000000000000000000000000000000000000000000000000000000004a0"
                        + "00000000000000000000000000000000000000000000000000000000000005a0"
                        + "00000000000000000000000000000000000000000000000000000000000008e0"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000160"
                        + "0000000000000000000000000000000000000000000000000000000000000220"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "3400000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000009"
                        + "6e6573746564466f6f0000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "3400000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000009"
                        + "6e6573746564466f6f0000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000003"
                        + "000000000000000000000000000000000000000000000000000000000000007b"
                        + "000000000000000000000000000000000000000000000000000000000000007b"
                        + "000000000000000000000000000000000000000000000000000000000000000c"
                        + "0000000000000000000000000000000000000000000000000000000000000021"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000003"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000160"
                        + "0000000000000000000000000000000000000000000000000000000000000260"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "3400000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000009"
                        + "6e6573746564466f6f0000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "3400000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000009"
                        + "6e6573746564466f6f0000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000120"
                        + "00000000000000000000000000000000000000000000000000000000000001e0"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000";

        assertEquals(
                expected,
                TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }

    @Test
    public void testABIv2ConstructorEncode() {
        class Struct1 extends DynamicStruct {
            public String id;
            public String name;

            public Struct1(String id, String name) {
                super(new Utf8String(id), new Utf8String(name));
                this.id = id;
                this.name = name;
            }
        }
        class Struct2 extends StaticStruct {
            public BigInteger id;
            public BigInteger name;

            public Struct2(BigInteger id, BigInteger name) {
                super(new Uint256(id), new Uint256(name));
                this.id = id;
                this.name = name;
            }
        }

        assertEquals(
                "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000064"
                        + "0000000000000000000000000000000000000000000000000000000000000001"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6461746100000000000000000000000000000000000000000000000000000000",
                TestUtils.bytesToString(FunctionEncoder.encodeConstructor(
                        Arrays.asList(
                                new Struct1("id", "data"),
                                new Struct2(BigInteger.valueOf(100), BigInteger.ONE)))));
    }

    @Test
    public void testABIv2DynamicArrayEncode() {
        Function function =
                new Function(
                        "setFooDynamicArray",
                        Collections.singletonList(
                                new DynamicArray<>(
                                        TestFixture.Foo.class,
                                        new TestFixture.Foo("", ""),
                                        new TestFixture.Foo("id", "name"),
                                        new TestFixture.Foo("", ""))),
                        Collections.emptyList());
        assertEquals(
                "8907a08c"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000003"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "00000000000000000000000000000000000000000000000000000000000000e0"
                        + "00000000000000000000000000000000000000000000000000000000000001a0"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000080"
                        + "0000000000000000000000000000000000000000000000000000000000000002"
                        + "6964000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000004"
                        + "6e616d6500000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000040"
                        + "0000000000000000000000000000000000000000000000000000000000000060"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000",
                TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }

    @Test
    public void testEncodeStaticStructStaticArray() {
        Function function =
                new Function(
                        "setBarStaticArray",
                        Arrays.<Type>asList(
                                new StaticArray3(
                                        TestFixture.Bar.class,
                                        new TestFixture.Bar(
                                                BigInteger.valueOf(0), BigInteger.valueOf(0)),
                                        new TestFixture.Bar(
                                                BigInteger.valueOf(123), BigInteger.valueOf(123)),
                                        new TestFixture.Bar(
                                                BigInteger.valueOf(0), BigInteger.valueOf(0)))),
                        Arrays.asList());
        String expected =
                "8efa5af7"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "000000000000000000000000000000000000000000000000000000000000007b"
                        + "000000000000000000000000000000000000000000000000000000000000007b"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000";

        assertEquals(expected, TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }

    @Test
    public void testEncodeStaticStructDynamicArray() {
        Function function =
                new Function(
                        "setBarDynamicArray",
                        Arrays.<Type>asList(
                                new DynamicArray(
                                        TestFixture.Bar.class,
                                        new TestFixture.Bar(
                                                BigInteger.valueOf(0), BigInteger.valueOf(0)),
                                        new TestFixture.Bar(
                                                BigInteger.valueOf(123), BigInteger.valueOf(123)),
                                        new TestFixture.Bar(
                                                BigInteger.valueOf(0), BigInteger.valueOf(0)))),
                        Arrays.asList());
        String expected =
                "e1f84cb5"
                        + "0000000000000000000000000000000000000000000000000000000000000020"
                        + "0000000000000000000000000000000000000000000000000000000000000003"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "000000000000000000000000000000000000000000000000000000000000007b"
                        + "000000000000000000000000000000000000000000000000000000000000007b"
                        + "0000000000000000000000000000000000000000000000000000000000000000"
                        + "0000000000000000000000000000000000000000000000000000000000000000";

        assertEquals(expected, TestUtils.bytesToString(ecdsaEncoder.encode(function)));
    }
}
