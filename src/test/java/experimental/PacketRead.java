package experimental;

import packets.Packet;
import packets.data.ObjectData;
import packets.incoming.*;
import packets.outgoing.*;

import java.util.HashMap;
import java.util.Map;

public class PacketRead {

    public static void readAll(Packet packet) {
        // spammy
        if (packet instanceof NewTickPacket) return;
        if (packet instanceof PingPacket) return;
        if (packet instanceof PongPacket) return;
        if (packet instanceof MovePacket) return;
        if (packet instanceof PlayerShootPacket) return;
        if (packet instanceof ServerPlayerShootPacket) return;
        // common
//        if (packet instanceof ShowEffectPacket) return;
        if (packet instanceof InvResultPacket) return;
        if (packet instanceof UpdateAckPacket) return;
        if (packet instanceof TextPacket) return;
        if (packet instanceof ChangeAllyShootPacket) return;
        if (packet instanceof EnemyShootPacket) return;
        if (packet instanceof EnemyHitPacket) return;
        if (packet instanceof DamagePacket) return;
        if (packet instanceof OtherHitPacket) return;
        if (packet instanceof AoePacket) return;
        if (packet instanceof AoeAckPacket) return;
        if (packet instanceof PlayerHitPacket) return;
        if (packet instanceof PlayerTextPacket) return;
        if (packet instanceof GotoPacket) return;
        // load packet
        if (packet instanceof QuestObjectIdPacket) return;
        if (packet instanceof LoadPacket) return;
        if (packet instanceof MapInfoPacket) return;
        if (packet instanceof HelloPacket) return;
        if (packet instanceof ReconnectPacket) return;
        if (packet instanceof ExaltationUpdatePacket) return;
        if (packet instanceof ShootAckCounterPacket) return;
        if (packet instanceof NotificationPacket) return;
        if (packet instanceof ForgeUnlockedBlueprints) return;
        if (packet instanceof QuestFetchResponsePacket) return;
        // usage
//        if (packet instanceof UseItemPacket) return;
        if (packet instanceof UsePortalPacket) return;
        if (packet instanceof ClientStatPacket) return;
        if (packet instanceof DashPacket) return;
        if (packet instanceof DashAckPacket) return;
        if (packet instanceof EscapePacket) return;
        if (packet instanceof InvSwapPacket) return;
        // unknown
        if (packet instanceof UnknownPacket139) return;
        if (packet instanceof GotoAckPacket) return;
        // RealmHeroesLeftPacket
        if (packet instanceof RealmHeroesLeftPacket) return;

//        if (packet instanceof CreateSuccessPacket) return;

        if (packet instanceof UpdatePacket) {
//            crystalTPRange((UpdatePacket) packet);
            return;
        }
        if (packet instanceof VaultContentPacket) {
//            countPots((VaultContentPacket) packet);
            return;
        }

        //RealmHeroesLeftPacket

        System.out.println(packet);
    }

    private static void countPots(VaultContentPacket p) {
        HashMap<Integer, Integer> pots = new HashMap<>();
        for (int i : p.potionContents) {
            if (pots.containsKey(i)) {
                pots.put(i, pots.get(i) + 1);
            } else {
                pots.put(i, 1);
            }
        }
        for (int i : p.vaultContents) {
            if (pots.containsKey(i)) {
                pots.put(i, pots.get(i) + 1);
            } else {
                pots.put(i, 1);
            }
        }
        for (int i : p.giftContents) {
            if (pots.containsKey(i)) {
                pots.put(i, pots.get(i) + 1);
            } else {
                pots.put(i, 1);
            }
        }
        int[] stats = new int[8];
        String[] names = {"Attack", "Defense", "Speed", "Vitality", "Wisdom", "Dexterity", "Life", "Mana"};
        for (Map.Entry<Integer, Integer> m : pots.entrySet()) {
            int k = m.getKey();
            int v = m.getValue();
            int mult = 1;
            if (k >= 9064 && k <= 9071) mult = 2;
            addPotion(stats, k, v * mult);
        }
        for (int index = 0; index < 8; index++) {
            System.out.printf("%s=%d\n", names[index], stats[index]);
        }
    }

    private static void addPotion(int[] stats, int id, int i) {
        if (2591 == id) stats[0] += i;             // Attack
        if (2592 == id) stats[1] += i;             // Defense
        if (2593 == id) stats[2] += i;             // Speed
        if (2612 == id) stats[3] += i;             // Vitality
        if (2613 == id) stats[4] += i;             // Wisdom
        if (2636 == id) stats[5] += i;             // Dexterity
        if (2793 == id) stats[6] += i;             // Life
        if (2794 == id) stats[7] += i;             // Mana
        if (5465 == id) stats[0] += i;             // Attack
        if (5466 == id) stats[1] += i;             // Defense
        if (5467 == id) stats[2] += i;             // Speed
        if (5468 == id) stats[3] += i;             // Vitality
        if (5469 == id) stats[4] += i;             // Wisdom
        if (5470 == id) stats[5] += i;             // Dexterity
        if (5471 == id) stats[6] += i;             // Life
        if (5472 == id) stats[7] += i;             // Mana
        if (9064 == id) stats[0] += i;             // Attack
        if (9065 == id) stats[1] += i;             // Defense
        if (9066 == id) stats[2] += i;             // Speed
        if (9067 == id) stats[3] += i;             // Vitality
        if (9068 == id) stats[4] += i;             // Wisdom
        if (9069 == id) stats[5] += i;             // Dexterity
        if (9070 == id) stats[6] += i;             // Life
        if (9071 == id) stats[7] += i;             // Mana
    }

    static float xsave = 0;
    static float ysave = 0;

    private static void crystalTPRange(UpdatePacket packet) {
        float x = packet.pos.x;
        float y = packet.pos.y;
        for (ObjectData od : packet.newObjects) {
            if (od.objectType == 10025) {
                System.out.println(od.status.pos);
                xsave = od.status.pos.x;
                ysave = od.status.pos.y;
            }
        }
        if (x != 0 && y != 0) {
            System.out.printf("crystal:%.2f   TP spot:%.2f\n", dist(x, y, xsave, ysave), dist(x, y, xsave, ysave + 6));
        }
    }

    private static double dist(float x1, float y1, float x2, float y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }
}
