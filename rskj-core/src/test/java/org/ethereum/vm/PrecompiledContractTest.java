/*
 * This file is part of RskJ
 * Copyright (C) 2017 RSK Labs Ltd.
 * (derived from ethereumJ library, Copyright (c) 2016 <ether.camp>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package org.ethereum.vm;

import co.rsk.config.TestSystemProperties;
import org.bouncycastle.util.encoders.Hex;
import org.ethereum.util.BIUtil;
import org.ethereum.util.ByteUtil;
import org.ethereum.vm.PrecompiledContracts.PrecompiledContract;
import org.ethereum.vm.exception.VMException;
import org.junit.Ignore;
import org.junit.Test;

import java.math.BigInteger;

import static org.ethereum.util.ByteUtil.EMPTY_BYTE_ARRAY;
import static org.junit.Assert.*;

/**
 * @author Roman Mandeleil
 */
public class PrecompiledContractTest {


    private final TestSystemProperties config = new TestSystemProperties();
    private final PrecompiledContracts precompiledContracts = new PrecompiledContracts(config, null);

    @Test
    public void identityTest1() throws VMException {

        DataWord addr = DataWord.valueFromHex("0000000000000000000000000000000000000000000000000000000000000004");
        PrecompiledContract contract = precompiledContracts.getContractForAddress(null, addr);
        byte[] data = Hex.decode("112233445566");
        byte[] expected = Hex.decode("112233445566");

        byte[] result = contract.execute(data);

        assertArrayEquals(expected, result);
    }


    @Test
    public void sha256Test1() throws VMException {

        DataWord addr = DataWord.valueFromHex("0000000000000000000000000000000000000000000000000000000000000002");
        PrecompiledContract contract = precompiledContracts.getContractForAddress(null, addr);
        byte[] data = null;
        String expected = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

        byte[] result = contract.execute(data);

        assertEquals(expected, ByteUtil.toHexString(result));
    }

    @Test
    public void sha256Test2() throws VMException {

        DataWord addr = DataWord.valueFromHex("0000000000000000000000000000000000000000000000000000000000000002");
        PrecompiledContract contract = precompiledContracts.getContractForAddress(null, addr);
        byte[] data = ByteUtil.EMPTY_BYTE_ARRAY;
        String expected = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

        byte[] result = contract.execute(data);

        assertEquals(expected, ByteUtil.toHexString(result));
    }

    @Test
    public void sha256Test3() throws VMException {

        DataWord addr = DataWord.valueFromHex("0000000000000000000000000000000000000000000000000000000000000002");
        PrecompiledContract contract = precompiledContracts.getContractForAddress(null, addr);
        byte[] data = Hex.decode("112233");
        String expected = "49ee2bf93aac3b1fb4117e59095e07abe555c3383b38d608da37680a406096e8";

        byte[] result = contract.execute(data);

        assertEquals(expected, ByteUtil.toHexString(result));
    }

    @Test
    public void Ripempd160Test1() throws VMException {

        DataWord addr = DataWord.valueFromHex("0000000000000000000000000000000000000000000000000000000000000003");
        PrecompiledContract contract = precompiledContracts.getContractForAddress(null, addr);
        byte[] data = Hex.decode("0000000000000000000000000000000000000000000000000000000000000001");
        String expected = "000000000000000000000000ae387fcfeb723c3f5964509af111cf5a67f30661";

        byte[] result = contract.execute(data);

        assertEquals(expected, ByteUtil.toHexString(result));
    }

    @Test @Ignore("expected != result, inherited test")
    public void ecRecoverTest1() throws VMException {

        byte[] data = Hex.decode("18c547e4f7b0f325ad1e56f57e26c745b09a3e503d86e00e5255ff7f715d3d1c000000000000000000000000000000000000000000000000000000000000001c73b1693892219d736caba55bdb67216e485557ea6b6af75f37096c9aa6a5a75feeb940b1d03b21e36b0e47e79769f095fe2ab855bd91e3a38756b7d75a9c4549");
        DataWord addr = DataWord.valueFromHex("0000000000000000000000000000000000000000000000000000000000000001");
        PrecompiledContract contract = precompiledContracts.getContractForAddress(null, addr);
        String expected = "000000000000000000000000ae387fcfeb723c3f5964509af111cf5a67f30661";

        byte[] result = contract.execute(data);

        System.out.println(ByteUtil.toHexString(result));

        // todo(fedejinich) analyse this case
        assertEquals(expected, ByteUtil.toHexString(result));
    }
    @Test
    public void modExpTest() throws VMException {

        DataWord addr = DataWord.valueFromHex("0000000000000000000000000000000000000000000000000000000000000005");

        PrecompiledContract contract = precompiledContracts.getContractForAddress(null, addr);
        assertNotNull(contract);

        byte[] data1 = Hex.decode(
                "0000000000000000000000000000000000000000000000000000000000000001" +
                        "0000000000000000000000000000000000000000000000000000000000000020" +
                        "0000000000000000000000000000000000000000000000000000000000000020" +
                        "03" +
                        "fffffffffffffffffffffffffffffffffffffffffffffffffffffffefffffc2e" +
                        "fffffffffffffffffffffffffffffffffffffffffffffffffffffffefffffc2f");

        assertEquals(13056, contract.getGasForData(data1));

        byte[] res1 = contract.execute(data1);
        assertEquals(32, res1.length);
        assertEquals(BigInteger.ONE, BIUtil.toBI(res1));

        byte[] data2 = Hex.decode(
                "0000000000000000000000000000000000000000000000000000000000000000" +
                        "0000000000000000000000000000000000000000000000000000000000000020" +
                        "0000000000000000000000000000000000000000000000000000000000000020" +
                        "fffffffffffffffffffffffffffffffffffffffffffffffffffffffefffffc2e" +
                        "fffffffffffffffffffffffffffffffffffffffffffffffffffffffefffffc2f");

        assertEquals(13056, contract.getGasForData(data2));

        byte[] res2 = contract.execute(data2);
        assertEquals(32, res2.length);
        assertEquals(BigInteger.ZERO, BIUtil.toBI(res2));

        byte[] data3 = Hex.decode(
                "0000000000000000000000000000000000000000000000000000000000000000" +
                        "0000000000000000000000000000000000000000000000000000000000000020" +
                        "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                        "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe" +
                        "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffd");

        // hardly imagine this value could be a real one
        assertEquals(3_674_950_435_109_146_392L, contract.getGasForData(data3));

        byte[] data4 = Hex.decode(
                "0000000000000000000000000000000000000000000000000000000000000001" +
                        "0000000000000000000000000000000000000000000000000000000000000002" +
                        "0000000000000000000000000000000000000000000000000000000000000020" +
                        "03" +
                        "ffff" +
                        "8000000000000000000000000000000000000000000000000000000000000000" +
                        "07"); // "07" should be ignored by data parser

        assertEquals(768, contract.getGasForData(data4));

        byte[] res4 = contract.execute(data4);
        assertEquals(32, res4.length);
        assertEquals(new BigInteger("26689440342447178617115869845918039756797228267049433585260346420242739014315"), BIUtil.toBI(res4));

        byte[] data5 = Hex.decode(
                "0000000000000000000000000000000000000000000000000000000000000001" +
                        "0000000000000000000000000000000000000000000000000000000000000002" +
                        "0000000000000000000000000000000000000000000000000000000000000020" +
                        "03" +
                        "ffff" +
                        "80"); // "80" should be parsed as "8000000000000000000000000000000000000000000000000000000000000000"
        // cause call data is infinitely right-padded with zero bytes

        assertEquals(768, contract.getGasForData(data5));

        byte[] res5 = contract.execute(data5);
        assertEquals(32, res5.length);
        assertEquals(new BigInteger("26689440342447178617115869845918039756797228267049433585260346420242739014315"), BIUtil.toBI(res5));

        // check overflow handling in gas calculation
        byte[] data6 = Hex.decode(
                "0000000000000000000000000000000000000000000000000000000000000020" +
                        "0000000000000000000000000000000020000000000000000000000000000000" +
                        "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                        "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe" +
                        "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffd" +
                        "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffd");

        assertEquals(Long.MAX_VALUE, contract.getGasForData(data6));

        // check rubbish data
        byte[] data7 = Hex.decode(
                "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                        "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                        "ffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffff" +
                        "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffe" +
                        "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffd" +
                        "fffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffffd");

        assertEquals(Long.MAX_VALUE, contract.getGasForData(data7));

        // check empty data
        byte[] data8 = new byte[0];

        assertEquals(0, contract.getGasForData(data8));

        byte[] res8 = contract.execute(data8);
        assertArrayEquals(EMPTY_BYTE_ARRAY, res8);

        assertEquals(0, contract.getGasForData(null));
        assertArrayEquals(EMPTY_BYTE_ARRAY, contract.execute(null));
    }

}
