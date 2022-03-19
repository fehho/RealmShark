package packets.packetcapture;

import example.gui.TomatoBandwidth;
import packets.Packet;
import packets.PacketType;
import packets.packetcapture.encryption.RC4;
import packets.packetcapture.encryption.RotMGRC4Keys;
import packets.packetcapture.logger.PacketLogger;
import packets.packetcapture.networktap.Sniffer;
import packets.packetcapture.networktap.netpackets.TcpPacket;
import packets.packetcapture.pconstructor.PConstructor;
import packets.packetcapture.pconstructor.PacketConstructor;
import packets.packetcapture.register.Register;
import packets.reader.BufferReader;
import util.Util;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * The core class to process packets. First the network tap is sniffed to receive all packets. The packets
 * are filtered for port 2050, the rotmg port, and TCP packets. Then the packets are stitched together in
 * streamConstructor and rotmgConstructor class. After the packets are constructed the RC4 cipher is used
 * decrypt the data. The data is then matched with target classes and emitted through the registry.
 */
public class PacketProcessor extends Thread {
    private PConstructor incomingPacketConstructor;
    private PConstructor outgoingPacketConstructor;
    private Sniffer sniffer;
    private PacketLogger logger;

    /**
     * Basic constructor of packetProcessor
     * TODO: Add linux and mac support later
     */
    public PacketProcessor() {
        sniffer = new Sniffer(this);
        incomingPacketConstructor = new PacketConstructor(this, new RC4(RotMGRC4Keys.INCOMING_STRING));
        outgoingPacketConstructor = new PacketConstructor(this, new RC4(RotMGRC4Keys.OUTGOING_STRING));
        logger = new PacketLogger();
    }

    /**
     * Start method for PacketProcessor.
     */
    public void run() {
        tapPackets();
    }

    /**
     * Stop method for PacketProcessor.
     */
    public void stopSniffer() {
        sniffer.closeSniffers();
    }

    /**
     * Method to start the packet sniffer that will send packets back to receivedPackets.
     */
    public void tapPackets() {
        incomingPacketConstructor.startResets();
        outgoingPacketConstructor.startResets();
        logger.startLogger();
        try {
            sniffer.startSniffer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method for retrieving TCP packets incoming from the sniffer.
     *
     * @param packet The TCP packets retrieved from the network tap.
     */
    public void receivedPackets(TcpPacket packet) {
        // 2050 is default rotmg server port.
        if (packet.getSrcPort() == 2050) {
            constIncomingPackets(packet); // Incoming packets have 2050 source port.
        } else if (packet.getDstPort() == 2050) {
            constOutgoingPackets(packet);// Outgoing packets have destination port set to 2050.
        }
        TomatoBandwidth.setInfo(logger.toString()); // update info GUI if open // TODO: remove this bad garbage asap
    }

    /**
     * Incoming packets from rotmg servers.
     *
     * @param packet Incoming TCP packet
     */
    private void constIncomingPackets(TcpPacket packet) {
        logger.addIncoming(packet.getRawData().length);
        incomingPacketConstructor.build(packet);
    }

    /**
     * Outgoing packets to rotmg servers.
     *
     * @param packet Outgoing TCP packet
     */
    private void constOutgoingPackets(TcpPacket packet) {
        logger.addOutgoing(packet.getRawData().length);
        outgoingPacketConstructor.build(packet);
    }

    /**
     * Completed packets constructed by stream and rotmg constructor returned to packet constructor.
     * Decoded by the cipher and sent back to the processor to be emitted to subscribed users.
     *
     * @param type Constructed packet type.
     * @param size
     * @param data Constructed packet data.
     */
    public void processPackets(byte type, int size, ByteBuffer data) {
        if (!PacketType.containsKey(type)) {
            System.err.println("Unknown packet type:" + type + " Data:" + Arrays.toString(data.array()));
            return;
        }
        logger.addPacket(type, size);
        Packet packetType = PacketType.getPacket(type).factory();
        BufferReader pData = new BufferReader(data);

        try {
            packetType.deserialize(pData);
            if (!pData.isBufferFullyParsed())
                pData.printError(packetType);
        } catch (Exception e) {
            Util.print("Buffer exploded: " + pData.getIndex() + "/" + pData.size());
            debugPackets(type, data);
            return;
        }
        Register.INSTANCE.emit(packetType);
    }

    /**
     * Helper for debugging packets
     */
    private void debugPackets(int type, ByteBuffer data) {
        Packet packetType = PacketType.getPacket(type).factory();
        try {
            Util.print(PacketType.byOrdinal(type) + "");
            data.position(5);
            BufferReader pDebug = new BufferReader(data);
            pDebug.printError(packetType);
            packetType.deserialize(pDebug);
        } catch (Exception e) {
            Util.print(Arrays.toString(e.getStackTrace()).replaceAll(", ", "\n"));
        }
    }
}
