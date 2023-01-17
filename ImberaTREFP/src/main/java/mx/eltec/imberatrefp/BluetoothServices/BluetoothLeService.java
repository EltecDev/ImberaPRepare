package mx.eltec.imberatrefp.BluetoothServices;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BluetoothLeService extends Service {
    private final static String TAG = BluetoothLeService.class.getSimpleName();
    private final IBinder mBinder = new LocalBinder();
    List<String> listData = new ArrayList<>();
    byte[] listDataB;
    StringBuilder sb = new StringBuilder();


    int payload = 20;
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.bluetooth.le.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    public final static UUID UUID_TREFPB_RW = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    public final static UUID UUID_TREFPB_SERVICE = UUID.fromString("0000ffe0-0000-1000-8000-00805f9b34fb");
    private static final UUID BLUETOOTH_LE_CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public BluetoothGattCharacteristic writeCharacteristic,readCharacteristic;

    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" + mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }



        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                gatt.requestMtu(512);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            //comentado para hacer función que solo manda bytes en lugar de telemetría}
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            //broadcastUpdateBytes(BluetoothLeService.ACTION_DATA_AVAILABLE,characteristic);
        }
        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            //comentado para hacer función que solo manda bytes en lugar de telemetría}
            //broadcastUpdateBytes(BluetoothLeService.ACTION_DATA_AVAILABLE,characteristic);
            broadcastUpdate(BluetoothLeService.ACTION_DATA_AVAILABLE,characteristic);
        }


        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if(status != BluetoothGatt.GATT_SUCCESS) {
                return;
            }
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if(descriptor.getCharacteristic() == readCharacteristic) {
                if (status != BluetoothGatt.GATT_SUCCESS) {

                } else {

                }
            }
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            if(status ==  BluetoothGatt.GATT_SUCCESS) {
                payload = mtu - 3;
            }
            connectCharacteristics3(gatt);
        }
    };

    public void connectCharacteristics3(BluetoothGatt gatt) {

        writeCharacteristic = mBluetoothGatt.getService(UUID_TREFPB_SERVICE).getCharacteristic(UUID_TREFPB_RW);
        readCharacteristic = mBluetoothGatt.getService(UUID_TREFPB_SERVICE).getCharacteristic(UUID_TREFPB_RW);

        int writeProperties = writeCharacteristic.getProperties();
        if((writeProperties & (BluetoothGattCharacteristic.PROPERTY_WRITE +
                BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE)) ==0) {
            return;
        }
        if(!gatt.setCharacteristicNotification(readCharacteristic,true)) {
            return;
        }
        BluetoothGattDescriptor readDescriptor = readCharacteristic.getDescriptor(BLUETOOTH_LE_CCCD);
        if(readDescriptor == null) {
            //Log.d("connectCharacteristics3","no CCCD descriptor for read characteristic");
            return;
        }
        int readProperties = readCharacteristic.getProperties();
        if((readProperties & BluetoothGattCharacteristic.PROPERTY_INDICATE) != 0) {
            readDescriptor.setValue(BluetoothGattDescriptor.ENABLE_INDICATION_VALUE);
        }else if((readProperties & BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
            readDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        } else {
            return;
        }
        if(!gatt.writeDescriptor(readDescriptor)) {
            //Log.d("connectCharacteristics3","FAIL");
        }
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        if (UUID_TREFPB_RW.equals(characteristic.getUuid())) {
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                listData.add(stringBuilder.toString());
            }
        } else {
            // No es la misma característica...
            final byte[] data = characteristic.getValue();
            if (data != null && data.length > 0) {
                final StringBuilder stringBuilder = new StringBuilder(data.length);
                for(byte byteChar : data)
                    stringBuilder.append(String.format("%02X ", byteChar));
                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
            }
        }
        sendBroadcast(intent);
    }

    public List<String> getDataFromBroadcastUpdate(){
        List<String> listData2 = new ArrayList<>(listData);
        listData.clear();
        return listData2;
    }

    public String getDataFromBroadcastUpdateString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (String s:listData)
            stringBuilder.append(s);

        return stringBuilder.toString().trim();
    }

    public void clearData(){
        listData.clear();
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    public boolean initialize() {
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            return false;
        }

        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;

                return true;
            } else {
                return false;
            }
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            return false;
        }

        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }


    public void disconnect() {
        if ( mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }else{
            writeCharacteristic = null;
            readCharacteristic = null;
            mBluetoothGatt.disconnect();
            mBluetoothGatt =null;
            listData.clear();
        }
    }


    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    public boolean sendFirstComando(String command){
        listData.clear();
        if (writeCharacteristic==null || readCharacteristic==null){
            return false;
        }else {
            byte[] b = hexStringToByteArray(command);
            writeCharacteristic.setValue(b);
            if (!mBluetoothGatt.writeCharacteristic(writeCharacteristic)) {
                Log.d(TAG,"WRITE FAIL");
                return false;
            } else {
                Log.d(TAG,"write started, len="+b.length);
                return true;
            }
        }
    }

    public void sendComando(String command){
        listData.clear();
        if (writeCharacteristic==null || readCharacteristic==null){
            Log.d(TAG,"writeCharacteristic NULL");
        }else {
            byte[] b = hexStringToByteArray(command);
            writeCharacteristic.setValue(b);
            if (!mBluetoothGatt.writeCharacteristic(writeCharacteristic)) {
                Log.d(TAG,"WRITE FAIL");
            } else {
                Log.d(TAG,"write started, len="+b.length);
            }
        }
    }


    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)+Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }



}
