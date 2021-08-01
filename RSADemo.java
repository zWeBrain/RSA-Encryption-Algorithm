package DM;
import java.math.BigInteger;

public class RSADemo {
    public static void main(String[] args) {
        String plaintext = "Love!";
        Generator lcg = new Generator(new BigInteger(  // a c seed mod
                Long.toString(25214903917L)),
                new BigInteger(Integer.toString(37)),
                new BigInteger(Long.toString(System.currentTimeMillis())), // a random number
                BigInteger.TWO.pow(48));
        lcg.nextInt();
        long p, q;
        do p = Long.parseLong(lcg.nextInt().toString()); while (!isPrime(p));
        do q = Long.parseLong(lcg.nextInt().toString()); while (!isPrime(q));
        BigInteger Bp = BigInteger.valueOf(p);
        BigInteger Bq = BigInteger.valueOf(q);
        BigInteger Bn = Bp.multiply(Bq);
        BigInteger Bphi = Bp.subtract(BigInteger.ONE).multiply(Bq.subtract(BigInteger.ONE));
        BigInteger Be = BigInteger.TWO.add(BigInteger.ONE); // 3
        while (Bphi.mod(Be).equals(BigInteger.ZERO)) Be = Be.add(BigInteger.ONE);
        BigInteger Bk = BigInteger.ONE;
        BigInteger Bd;
        while (true) {
            if ((Bk.multiply(Bphi).add(BigInteger.ONE)).mod(Be).equals(BigInteger.ZERO)) {
                Bd = (Bk.multiply(Bphi).add(BigInteger.ONE)).divide(Be);
                break;
            } else Bk = Bk.add(BigInteger.ONE);
        }
        Encrypt encrypt = new Encrypt(plaintext, Bn, Be); // message m k
        BigInteger encryptedtext = encrypt.encrypt();
        Decrypt decrypt = new Decrypt(Bn, Bd, encryptedtext);
        String decryptedtext = decrypt.decrypt();

        System.out.println("plaintext = " + plaintext);
        System.out.println("p = " + p);
        System.out.println("q = " + q);
        System.out.println("n = " + Bn);
        System.out.println("phi = " + Bphi);
        System.out.println("e = " + Be);
        System.out.println("k = " + Bk);
        System.out.println("d = " + Bd);
        System.out.println("encryptedtext = " + encryptedtext);
        System.out.println("decryptedtext = " + getMessage(decryptedtext));
    }

    private static String getMessage(String mes) {
        int digit = 3;
        StringBuilder mesBuilder = new StringBuilder(mes);
        while (mesBuilder.length() % 3 != 0) {
            mesBuilder.insert(0, "0");
        }
        mes = mesBuilder.toString();
        byte[] bytes = new byte[mes.length() / digit];
        for (int i = 0; i < mes.length(); i += digit) {
            bytes[i / digit] = (byte) Integer.parseInt(mes.substring(i, i + digit));
        }
        return new String(bytes);
    }

    static class Generator {
        private BigInteger A;
        private BigInteger C;
        private BigInteger Seed;
        private BigInteger Mod;

        Generator(BigInteger a, BigInteger c, BigInteger seed, BigInteger mod) {
            A = a;
            C = c;
            Seed = seed;
            Mod = mod;
        }

        public BigInteger nextInt() {
            Seed = A.multiply(Seed).add(C).mod(Mod);
            StringBuilder newInt = new StringBuilder(Seed.toString());
            while (newInt.length() < 16) {
                newInt.insert(0, "0");
            }
            return new BigInteger(newInt.toString().substring(1, 16));
        }
    }

    static class Decrypt {
        private BigInteger n;
        private BigInteger d;
        private BigInteger m;

        Decrypt(BigInteger n, BigInteger d, BigInteger m) {
            this.n = n;
            this.d = d;
            this.m = m;
        }

        String decrypt() {
            BigInteger mes = m.modPow(d, n);
            return mes.toString();
        }
    }

    static class Encrypt {
        private String message;
        private BigInteger n;
        private BigInteger e;

        Encrypt(String message, BigInteger n, BigInteger e) {
            this.message = message;
            this.n = n;
            this.e = e;
        }

        BigInteger encrypt() {
            byte[] bytes = message.getBytes();
            StringBuilder result = new StringBuilder();
            for (int i : bytes) result.append(String.format("%03d", i));
            String mes = result.toString();
            int length = String.valueOf(n.toString()).length();
            if (length < mes.length()) {
                System.out.println("It is too long!");
                return null;
            }
            BigInteger m = BigInteger.valueOf(Long.parseLong(mes));
            return m.modPow(e, n);
        }
    }

    private static boolean isPrime(long n) {
        long[] witness = new long[]{ 2, 7, 325, 9375, 28178, 450775, 9780504, 1795265022L };
        if (n == 1) return true;
        int s = Long.numberOfTrailingZeros(n - 1);
        long d = (n - 1) >> s;
        BigInteger bigD = new BigInteger(Long.toString(d));
        BigInteger bigN = new BigInteger(Long.toString(n));
        for (long l : witness) {
            if (n <= l) break;
            else if (MillerRabinTest(n, l, bigD, bigN, s)) {
                return true;
            }
        }
        if (n > witness[witness.length - 1]) {
            for (int i = 0; i < 20; i++) {
                if (MillerRabinTest(n, (long) (Math.random() * (n - 1) + 1), bigD, bigN, s)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean MillerRabinTest(long n, long a, BigInteger bigD, BigInteger bigN, int s) {
        BigInteger bigA = new BigInteger(Long.toString(a));
        long result = bigA.modPow(bigD, bigN).longValue();
        if (result == 1) return true;
        for (int j = 0; j < s; j++) {
            if (result == n - 1) return true;
            result = (result * result) % n;
        }
        return false;
    }
}
