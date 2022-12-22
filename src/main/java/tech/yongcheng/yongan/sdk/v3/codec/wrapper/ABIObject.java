package tech.yongcheng.yongan.sdk.v3.codec.wrapper;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.Address;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.Bool;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.Bytes;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.DynamicBytes;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.NumericType;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.Type;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.Utf8String;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.generated.Int256;
import tech.yongcheng.yongan.sdk.v3.codec.datatypes.generated.Uint256;

public class ABIObject {

    public enum ObjectType {
        VALUE, // uint, int, bool, address, bytes<M>, bytes, string
        STRUCT, // tuple
        LIST // T[], T[M]
    }

    public enum ValueType {
        BOOL, // bool
        UINT, // uint<M>
        INT, // int<M>
        BYTES, // byteN
        ADDRESS, // address
        STRING, // string
        DBYTES, // bytes
        FIXED, // fixed<M>x<N>
        UFIXED, // ufixed<M>x<N>
    }

    public enum ListType {
        DYNAMIC, // T[]
        FIXED, // T[M]
    }

    private String name; // field name

    private ObjectType type; // for value
    private ValueType valueType;

    private NumericType numericValue;
    private Bytes bytesValue;
    private int bytesLength;
    private Address addressValue;
    private Bool boolValue;
    private DynamicBytes dynamicBytesValue;
    private Utf8String stringValue;

    private ListType listType;
    private List<ABIObject> listValues; // for list
    private int listLength; // for list
    private ABIObject listValueType; // for list

    private List<ABIObject> structFields; // for struct

    public ABIObject(ObjectType type) {
        this.type = type;

        switch (type) {
            case VALUE:
                {
                    break;
                }
            case STRUCT:
                {
                    this.structFields = new LinkedList<ABIObject>();
                    break;
                }
            case LIST:
                {
                    this.listValues = new LinkedList<ABIObject>();
                    break;
                }
        }
    }

    public ABIObject(ValueType valueType) {
        this.type = ObjectType.VALUE;
        this.valueType = valueType;
    }

    public ABIObject(ValueType bytesValueType, int bytesLength) {
        this(bytesValueType);
        this.bytesLength = bytesLength;
    }

    public ABIObject(ListType listType) {
        this.type = ObjectType.LIST;
        this.listType = listType;
        this.listValues = new LinkedList<ABIObject>();
    }

    public ABIObject(Uint256 uintValue) {
        this(ValueType.UINT);
        this.numericValue = uintValue;
    }

    public ABIObject(Int256 intValue) {
        this(ValueType.INT);
        this.numericValue = intValue;
    }

    public ABIObject(Address addressValue) {
        this(ValueType.ADDRESS);
        this.addressValue = addressValue;
    }

    public ABIObject(Bool boolValue) {
        this(ValueType.BOOL);
        this.boolValue = boolValue;
    }

    public ABIObject(Utf8String stringValue) {
        this(ValueType.STRING);
        this.stringValue = stringValue;
    }

    public ABIObject(DynamicBytes dynamicBytesValue) {
        this(ValueType.DBYTES);
        this.dynamicBytesValue = dynamicBytesValue;
    }

    public ABIObject(Bytes bytesValue) {
        this(ValueType.BYTES);
        this.bytesValue = bytesValue;
    }

    public ABIObject(Bytes bytesValue, int bytesLength) {
        this(bytesValue);
        this.bytesLength = bytesLength;
    }

    public ABIObject newObjectWithoutValue() {
        ABIObject abiObject = new ABIObject(this.type);
        // value
        abiObject.setValueType(this.getValueType());
        abiObject.setName(this.getName());

        // list
        abiObject.setListType(this.getListType());
        abiObject.setListLength(this.getListLength());

        if (this.getListValueType() != null) {
            abiObject.setListValueType(this.getListValueType().newObjectWithoutValue());
        }

        if (this.listValues != null) {
            for (ABIObject obj : this.listValues) {
                abiObject.listValues.add(obj.newObjectWithoutValue());
            }
        }

        // tuple
        if (this.structFields != null) {
            for (ABIObject obj : this.structFields) {
                abiObject.structFields.add(obj.newObjectWithoutValue());
            }
        }

        return abiObject;
    }

    // clone itself
    public ABIObject newObject() {

        ABIObject abiObject = new ABIObject(this.type);
        abiObject.setBytesLength(this.bytesLength);

        // value
        abiObject.setValueType(this.getValueType());
        abiObject.setName(this.getName());

        if (this.getNumericValue() != null) {
            abiObject.setNumericValue(
                    new NumericType(
                            this.getNumericValue().getTypeAsString(),
                            this.getNumericValue().getValue(),
                            this.getNumericValue().getBitSize()) {});
        }

        if (this.getBoolValue() != null) {
            abiObject.setBoolValue(new Bool(this.getBoolValue().getValue()));
        }

        if (this.getStringValue() != null) {
            abiObject.setStringValue(new Utf8String(this.getStringValue().getValue()));
        }

        if (this.getDynamicBytesValue() != null) {
            abiObject.setDynamicBytesValue(
                    new DynamicBytes(this.getDynamicBytesValue().getValue()));
        }

        if (this.getAddressValue() != null) {
            abiObject.setAddressValue(new Address(this.getAddressValue().toUint160()));
        }

        if (this.getBytesValue() != null) {
            abiObject.setBytesValue(
                    new Bytes(
                            this.getBytesValue().getValue().length,
                            this.getBytesValue().getValue()));
        }

        // list
        abiObject.setListType(this.getListType());
        abiObject.setListLength(this.getListLength());

        if (this.getListValueType() != null) {
            abiObject.setListValueType(this.getListValueType().newObject());
        }

        if (this.listValues != null) {
            for (ABIObject obj : this.listValues) {
                abiObject.listValues.add(obj.newObject());
            }
        }

        // tuple
        if (this.structFields != null) {
            for (ABIObject obj : this.structFields) {
                abiObject.structFields.add(obj.newObject());
            }
        }

        return abiObject;
    }

    /**
     * Checks to see if the current type is dynamic
     *
     * @return true/false
     */
    public boolean isDynamic() {
        switch (this.type) {
            case VALUE:
                {
                    switch (this.valueType) {
                        case DBYTES: // bytes
                        case STRING: // string
                            return true;
                        default:
                            return false;
                    }
                }
            case LIST:
                {
                    switch (this.listType) {
                        case FIXED: // T[M]
                            {
                                return this.listValueType.isDynamic();
                            }
                        case DYNAMIC: // T[]
                            {
                                return true;
                            }
                    }
                    break;
                }
            case STRUCT:
                {
                    for (ABIObject abiObject : this.structFields) {
                        if (abiObject.isDynamic()) {
                            return true;
                        }
                    }
                    return false;
                }
        }

        return false;
    }

    /**
     * dynamic offset of this object
     *
     * @return the offset of the ABIObject
     */
    public int offset() {
        if (isDynamic()) { // dynamic
            return 1;
        }

        int offset = 0;
        if (this.type == ObjectType.VALUE) { // basic type
            offset = 1;
        } else if (this.type == ObjectType.STRUCT) { // tuple
            int l = 0;
            for (ABIObject abiObject : this.structFields) {
                l += abiObject.offset();
            }
            offset = l;
        } else { // T[M]
            int length = this.listLength;
            int basicOffset = this.listValueType.offset();
            offset = length * basicOffset;
        }

        return offset;
    }

    public int offsetAsByteLength() {
        return offset() * Type.MAX_BYTE_LENGTH;
    }

    public byte[] encode(boolean isWasm) throws IOException {
        return ContractCodecTools.encode(this, isWasm);
    }

    public ABIObject decode(byte[] input, boolean isWasm) throws ClassNotFoundException {
        return ContractCodecTools.decode(this, input, isWasm);
    }

    public ObjectType getType() {
        return this.type;
    }

    public void setType(ObjectType type) {
        this.type = type;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ValueType getValueType() {
        return this.valueType;
    }

    public NumericType getNumericValue() {
        return this.numericValue;
    }

    public Bool getBoolValue() {
        return this.boolValue;
    }

    public void setBoolValue(Bool boolValue) {
        this.type = ObjectType.VALUE;
        this.valueType = ValueType.BOOL;
        this.boolValue = boolValue;
    }

    public void setNumericValue(NumericType numericValue) {
        this.type = ObjectType.VALUE;
        if (numericValue.getTypeAsString().startsWith("int") || numericValue instanceof Int256) {
            this.valueType = ValueType.INT;
        } else {
            this.valueType = ValueType.UINT;
        }
        this.numericValue = numericValue;
    }

    public Bytes getBytesValue() {
        return this.bytesValue;
    }

    public void setBytesValue(Bytes bytesValue) {
        this.type = ObjectType.VALUE;
        this.valueType = ValueType.BYTES;
        this.bytesValue = bytesValue;
    }

    public Address getAddressValue() {
        return this.addressValue;
    }

    public void setAddressValue(Address addressValue) {
        this.type = ObjectType.VALUE;
        this.valueType = ValueType.ADDRESS;
        this.addressValue = addressValue;
    }

    public List<ABIObject> getStructFields() {
        return this.structFields;
    }

    public void setStructFields(List<ABIObject> structFields) {
        this.type = ObjectType.STRUCT;
        this.structFields = structFields;
    }

    public ListType getListType() {
        return this.listType;
    }

    public void setListType(ListType listType) {
        this.listType = listType;
    }

    public List<ABIObject> getListValues() {
        return this.listValues;
    }

    public void setListValues(List<ABIObject> listValues) {
        this.type = ObjectType.LIST;
        this.listValues = listValues;
    }

    public void setValueType(ValueType valueType) {
        this.valueType = valueType;
    }

    public DynamicBytes getDynamicBytesValue() {
        return this.dynamicBytesValue;
    }

    public void setDynamicBytesValue(DynamicBytes dynamicBytesValue) {
        this.dynamicBytesValue = dynamicBytesValue;
    }

    public Utf8String getStringValue() {
        return this.stringValue;
    }

    public void setStringValue(Utf8String stringValue) {
        this.stringValue = stringValue;
    }

    public ABIObject getListValueType() {
        return this.listValueType;
    }

    public void setListValueType(ABIObject listValueType) {
        this.listValueType = listValueType;
    }

    public int getListLength() {
        return this.listLength;
    }

    public void setListLength(int listLength) {
        this.listLength = listLength;
    }

    public int getBytesLength() {
        return this.bytesLength;
    }

    public void setBytesLength(int bytesLength) {
        this.bytesLength = bytesLength;
    }

    @Override
    public String toString() {

        String str = "ABIObject{" + "name='" + this.name + '\'' + ", type=" + this.type;

        if (this.type == ObjectType.VALUE) {
            str += ", valueType=" + this.valueType;
            switch (this.valueType) {
                case BOOL:
                    str += ", booValueType=";
                    str += Objects.isNull(this.boolValue) ? "null" : this.boolValue.getValue();
                    break;
                case UINT:
                case INT:
                    str += ", numericValue=";
                    str +=
                            Objects.isNull(this.numericValue)
                                    ? "null"
                                    : this.numericValue.getValue();
                    break;
                case ADDRESS:
                    str += ", addressValue=";
                    str +=
                            Objects.isNull(this.addressValue)
                                    ? "null"
                                    : this.addressValue.getValue();
                    break;
                case BYTES:
                    str += ", bytesValue=";
                    str += Objects.isNull(this.bytesValue) ? "null" : this.bytesValue.getValue();
                    break;
                case DBYTES:
                    str += ", dynamicBytesValue=";
                    str +=
                            Objects.isNull(this.dynamicBytesValue)
                                    ? "null"
                                    : this.dynamicBytesValue.getValue();
                    // case STRING:
                default:
                    str += ", stringValue=";
                    str += Objects.isNull(this.stringValue) ? "null" : this.stringValue.getValue();
            }
        } else if (this.type == ObjectType.LIST) {
            str += ", listType=" + this.listType;
            str += ", listValues=" + this.listValues + ", listLength=" + this.listLength;
        } else if (this.type == ObjectType.STRUCT) {
            str += ", structFields=" + this.structFields;
        }

        str += '}';
        return str;
    }
}
