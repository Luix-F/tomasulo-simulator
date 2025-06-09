//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Random;


class Mpis{
    public static void imprimirMatriz(String[][] matriz) {
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                System.out.print(matriz[i][j] + " ");
            }
            System.out.println(); // Pula linha após cada linha da matriz
        }
    }

    // Tabelas visuais
    String[][] VecInstructUnit;
    String[][] Registradores;
    String[][] RegRenomeacao;

    // Tabela de execução
    String[][] UnidadesFuncionais;

    // Tabela para as janelas de instruções
    String[][][] Estacao_de_Reserva;
    String[][] Estacao_de_Reserva2;
    String[][] Estacao_de_Reserva_ALU;
    String[][] Estacao_de_Reserva_mult;
    String[][] Estacao_de_Reserva_br;
    String[][] Estacao_de_Reserva_load;
    String[][] Estacao_de_Reserva_store;

    // Tamanho da janela de intruções
    static int Nji = 4;

    // Buffer de Reordenamento
    String[][] Reorder;

    // Ciclos por instrução
    static int Load = 2;
    static int Store = 2;
    static int Mult = 4;
    static int Add = 1;

    // quantas instruções serão decodificadas de um asó vez
    static int Nof = 2;

    // Clock
    int Ciclos = 0;
    // Program couter
    int Pc = 0;



    public void load(String[][] instruct){
        VecInstructUnit = instruct;
        String[][] R = {
                // Fixo
                {"$0", "0"}, {"$1", "1"},
                // Retornam valores de funções
                {"$v0", "vazio"}, {"$v1", "vazio"},
                // Argumentos
                {"$a0", "vazio"}, {"$a1", "vazio"},
                {"$a3", "vazio"},
                // Temporários
                {"$t0", "vazio"}, {"$t1", "vazio"},
                {"$t2", "vazio"}, {"$t3", "vazio"},
                {"$t4", "vazio"}, {"$t5", "vazio"},
                {"$t6", "vazio"}, {"$t7", "vazio"},
                {"$t8", "vazio"}, {"$t9", "vazio"},
                // Temporários salvos
                {"$s0", "vazio"}, {"$s1", "vazio"},
                {"$s2", "vazio"},{"$s3", "vazio"},
                {"$s4", "vazio"},{"$s5", "vazio"},
                {"$s6", "vazio"}, {"$t7", "vazio"},


        };
        Registradores = R;

        String RgR[][] = {// Renomeação
                // Nome, valor, referencia
                {"$ra", "vazio", "vazio"}, {"$rb", "vazio", "vazio"},
                {"$rc", "vazio", "vazio"}, {"$rd", "vazio", "vazio"},
                {"$re", "vazio", "vazio"}, {"$rf", "vazio", "vazio"},
        };
        Random rand = new Random();
        for (int i = 2; i < Registradores.length; i++) {
            Registradores[i][1] = "" + (rand.nextInt(100) + 1);
        }

        String UF[][] = {
                //UF,  ocupado, Reg,  Reg,    Reg/interge,tempo
                {"ADD", "N", "vazio", "vazio", "vazio", "0"},
                {"ADD", "N", "vazio", "vazio", "vazio", "0"},
                {"ADD", "N", "vazio", "vazio", "vazio", "0"},
                {"MULT", "N", "vazio", "vazio", "vazio", "0"},
                {"MULT", "N", "vazio", "vazio", "vazio", "0"},
                {"LD", "N", "vazio", "vazio", "vazio", "0"},
                {"LD", "N", "vazio", "vazio", "vazio", "0"},
                {"SW", "N", "vazio", "vazio", "vazio", "0"},
                {"SW", "N", "vazio", "vazio", "vazio", "0"}
        };
        UnidadesFuncionais = UF;
/*
        String er2[][] ={
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
        };
        Estacao_de_Reserva2 = er2;

        // Exemplo Estacao_de_Reserva
        String er[][][] = {
                {
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                },
                {
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                        {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                },
        };
        // Exemplo Estacao_de_Reserva
        Estacao_de_Reserva = new String[UnidadesFuncionais.length][Nji][6];
        for (int i = 0; i < UnidadesFuncionais.length; i++) {
            for (int j = 0; j < Nji; j++) {
                for (int k = 0; k < 6; k++) {
                    Estacao_de_Reserva[i][j][k] = "vazio";
                }
            }
        }*/
        String er[][] = {
                {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
        };

        Estacao_de_Reserva_ALU = er;
        Estacao_de_Reserva_mult = er;
        Estacao_de_Reserva_load = er;
        Estacao_de_Reserva_store = er;
        Estacao_de_Reserva_br = er;

        Reorder = new String[VecInstructUnit.length][3]; /*{
                //Instrução, Estado, valor
                {"vazio", "vazio", "vazio"}
        };*/
        for (int i = 0; i < VecInstructUnit.length; i++) {
            for (int j = 0; j < 3; j++) {
                Reorder[i][j] = "vazio";
            }
        }
        //Reorder = BF;
    }

    // Responsavel por verificar quantos ciclos uma instrução demora
    public int TempoDeExecucao(int id){
        if (UnidadesFuncionais[id][0].equals("ADD")){
            return Add;
        }else if (UnidadesFuncionais[id][0].equals("MULT")) {
            return Mult;
        }else if (UnidadesFuncionais[id][0].equals("LD")) {
            return Load;
        }else if (UnidadesFuncionais[id][0].equals("SW")) {
            return Store;
        }
        return -1;
    }

    // Atualiza a tabela de execução (Removenso as tarefas ja executadas)
    public void AtzPorCl(){
        for (int i = 0; i < UnidadesFuncionais.length; i++) {
            if (UnidadesFuncionais[i][1].equals("Y")){
                int end = TempoDeExecucao(i);
                Integer numero = Integer.valueOf(UnidadesFuncionais[i][5]); // Retorna um objeto Integer
                int valorPrimitivo = numero.intValue(); // Converte para int, se necessário
                valorPrimitivo++;
                if (valorPrimitivo < end){
                    UnidadesFuncionais[i][5] = "" + valorPrimitivo;
                }else {
                    UnidadesFuncionais[i][1] = "N";
                    for (int j = 2; j < UnidadesFuncionais[i].length-1; j++) {
                        UnidadesFuncionais[i][j] = "vazio";
                    }
                    UnidadesFuncionais[i][UnidadesFuncionais[i].length-1] = "0";
                }
            }
        }
    }

    // Despacha a instrução para a execução e atualiza a Estação de Reserva
    public int Despacho(){
        for (int i = 0; i < Estacao_de_Reserva_ALU.length; i++) {
            if (!(Estacao_de_Reserva_ALU[i][0].equals("OP"))){
                Exec(Estacao_de_Reserva_ALU[i]);
                for (int j = 1; j < Estacao_de_Reserva_ALU.length; j++) {
                    Estacao_de_Reserva_ALU[i-1] = Estacao_de_Reserva_ALU[i];
                }
                String er[] = {"OP", "ID", "VALOR", "ID", "VALOR", "ID"};
                Estacao_de_Reserva_ALU[Estacao_de_Reserva_ALU.length-1] = er;
            }
        }
        return 0;
    }

    public void Exec(String[] instrucao){
        //
    }

    // Posição da unidade funcional
    public int[] PosicaoUF(String op){
        if (op.equals("ADD")){
            int[] pointer= {0,2};
            return pointer;
        } else if (op.equals("SUB")) {
            int[] pointer= {0,2};
            return pointer;
        }else if (op.equals("MULT")) {
            int[] pointer= {3,4};
            return pointer;
        }else if (op.equals("LD")) {
            int[] pointer= {5,6};
            return pointer;
        }else if (op.equals("SW")) {
            int[] pointer= {7,8};
            return pointer;
        }else if (op.equals("DIV")) {
            int[] pointer= {3,4};
            return pointer;
        }else if (op.equals("SUB")) {
            int[] pointer= {0,2};
            return pointer;
        }
        int[] pointer= {0,2};
        return pointer;
    }

    // Carrega a tarefa para a execução
    public int Execucao(int r0, int r1, int r2, int[] instructionPoint){
        for (int i = instructionPoint[0]; i <= instructionPoint[1]; i++) {
            System.out.printf("UnidadesFuncionais[i][1]:" + UnidadesFuncionais[i][1] + " - " + i + "\n");
            if ((UnidadesFuncionais[i][1].equals("N"))){
                UnidadesFuncionais[i][1] = "Y";
                UnidadesFuncionais[i][2] = Registradores[r0][0];
                UnidadesFuncionais[i][3] = Registradores[r1][0];
                UnidadesFuncionais[i][4] = Registradores[r2][0];

                return 0;
            }
        }
        return 0;
    }

    // Estação de Reserva
    public int EstacaoDeReserva(int r0, int r1, int r2, int[] instructionPoint){
        for (int i = 0; i < Estacao_de_Reserva2.length; i++) {
            if (Estacao_de_Reserva2[i][0].equals("OP")){
                //   0     1     2     3        4     5        6
                // {"OP", "OP", "ID", "VALOR", "ID", "VALOR", "ID"},
                Estacao_de_Reserva2[i][0] = "" + instructionPoint[0];
                Estacao_de_Reserva2[i][1] = "" + instructionPoint[1];
                Estacao_de_Reserva2[i][2] = "" + r0;
                Estacao_de_Reserva2[i][4] = "" + r1;
                Estacao_de_Reserva2[i][6] = "" + r2;
            }
        }
        return 0;
    }

    public int Estacaodereserva_ALU(String[] s){
        for (int i = 0; i < Estacao_de_Reserva_ALU.length; i++) {
            if (Estacao_de_Reserva_ALU[i][1].equals("ID")){
                for (int j = 0; j < Estacao_de_Reserva_ALU[0].length; j++) {
                    Estacao_de_Reserva_ALU[i][j] = s[j];
                }
                Pc++;
                return 0;
            }
        }
        return 0;
    }

    public int Estacaodereserva_mult(String[] s){
        for (int i = 0; i < Estacao_de_Reserva_mult.length; i++) {
            if (Estacao_de_Reserva_mult[i][1].equals("ID")){
                for (int j = 0; j < Estacao_de_Reserva_mult[0].length; j++) {
                    Estacao_de_Reserva_mult[i][j] = s[j];
                }
                Pc++;
                return 0;
            }
        }
        return 0;
    }

    public int Estacaodereserva_load(String[] s){
        for (int i = 0; i < Estacao_de_Reserva_load.length; i++) {
            if (Estacao_de_Reserva_load[i][1].equals("ID")){
                for (int j = 0; j < Estacao_de_Reserva_load[0].length; j++) {
                    Estacao_de_Reserva_load[i][j] = s[j];
                }
                Pc++;
                return 0;
            }
        }
        return 0;
    }

    public int Estacaodereserva_store(String[] s){
        for (int i = 0; i < Estacao_de_Reserva_store.length; i++) {
            if (Estacao_de_Reserva_store[i][1].equals("ID")){
                for (int j = 0; j < Estacao_de_Reserva_store[0].length; j++) {
                    Estacao_de_Reserva_store[i][j] = s[j];
                }
                Pc++;
                return 0;
            }
        }
        return 0;
    }

    public int Estacaodereserva_br(String[] s){
        for (int i = 0; i < Estacao_de_Reserva_br.length; i++) {
            if (Estacao_de_Reserva_br[i][1].equals("ID")){
                for (int j = 0; j < Estacao_de_Reserva_br[0].length; j++) {
                    Estacao_de_Reserva_br[i][j] = s[j];
                }
                Pc++;
                return 0;
            }
        }
        return 0;
    }

    // Encontra registrador de renomeação livre
    public int Renomeacao(){
        for (int i = 0; i < RegRenomeacao.length; i++) {
            if (RegRenomeacao[i][1].equals("vazio")){
                return i;
            }
        }
        return 0;
    }

    // Indendifica dependencias
    public String[] Dependencias(int r0, int r1, int r2, String op){
        //   0     1     2        3     4        5
        // {"OP", "ID", "VALOR", "ID", "VALOR", "ID"},
        String ER[] = {"vazio", "vazio", "vazio", "vazio", "vazio", "vazio",};
        ER[2] = Registradores[r1][1];
        ER[4] = Registradores[r2][1];
        ER[1] = ""+r0;
        for (int i = 0; i < Estacao_de_Reserva2.length; i++) {
            String s1 = ""+r1;
            String s2 = ""+r2;
            String s0 = ""+r0;

            ER[0] = op;
            //ER[1] = ""+r0;
            if (Estacao_de_Reserva_ALU[i][1].equals(s1) || Estacao_de_Reserva_mult[i][1].equals(s1)){ //
                ER[3] = Registradores[r1][0];
                ER[2] = "vazio";
            }else
            if (Estacao_de_Reserva_ALU[i][1].equals(s2)) {
                ER[5] = Registradores[r2][0];
                ER[4] = "vazio";
            }else
            if((Estacao_de_Reserva_ALU[i][3].equals(s0) || Estacao_de_Reserva_ALU[i][5].equals(s0))){ // ant-dependência
                int rgr = Renomeacao();
                ER[1] = RegRenomeacao[rgr][0];
                RegRenomeacao[rgr][2] = RegRenomeacao[r0][0];
            }
        }
        return ER;
    }

    // Decodifica a Instrução
    public void Decode(int id){
        int a = -1; int b = -1; int c = -1;
        if (VecInstructUnit[id][0].equals("ADD")){
            for (int j = 0; j < Registradores.length; j++){
                if (VecInstructUnit[id][1].equals(Registradores[j][0]) && j > 1){
                    a = j;
                }
                if (VecInstructUnit[id][2].equals(Registradores[j][0])){
                    b = j;
                }
                if (VecInstructUnit[id][3].equals(Registradores[j][0])){
                    c = j;
                }
            }
            String[] s = Dependencias(a,b,c,VecInstructUnit[id][0]);
            Estacaodereserva_ALU(s);
        }else if(VecInstructUnit[id][0].equals("SUB")){
            for (int j = 0; j < Registradores.length; j++){
                if (VecInstructUnit[id][1].equals(Registradores[j][0]) && j > 1){
                    a = j;
                }
                if (VecInstructUnit[id][2].equals(Registradores[j][0])){
                    b = j;
                }
                if (VecInstructUnit[id][3].equals(Registradores[j][0])){
                    c = j;
                }
            }
            String[] s = Dependencias(a,b,c,VecInstructUnit[id][0]);
            Estacaodereserva_ALU(s);
        }else if(VecInstructUnit[id][0].equals("MULT")){
            for (int j = 0; j < Registradores.length; j++){
                if (VecInstructUnit[id][1].equals(Registradores[j][0]) && j > 1){
                    a = j;
                }
                if (VecInstructUnit[id][2].equals(Registradores[j][0])){
                    b = j;
                }
                if (VecInstructUnit[id][3].equals(Registradores[j][0])){
                    c = j;
                }
            }
            String[] s = Dependencias(a,b,c,VecInstructUnit[id][0]);
            Estacaodereserva_load(s);
        }else if(VecInstructUnit[id][0].equals("LD")){
            for (int j = 0; j < Registradores.length; j++){
                if (VecInstructUnit[id][1].equals(Registradores[j][0]) && j > 1){
                    a = j;
                }
                if (VecInstructUnit[id][2].equals(Registradores[j][0])){
                    b = j;
                }
                if (VecInstructUnit[id][3].equals(Registradores[j][0])){
                    c = j;
                }
            }
            String[] s = Dependencias(a,b,c,VecInstructUnit[id][0]);
            Estacaodereserva_load(s);
        }else if(VecInstructUnit[id][0].equals("SW")){
            for (int j = 0; j < Registradores.length; j++){
                if (VecInstructUnit[id][1].equals(Registradores[j][0]) && j > 1){
                    a = j;
                }
                if (VecInstructUnit[id][2].equals(Registradores[j][0])){
                    b = j;
                }
                if (VecInstructUnit[id][3].equals(Registradores[j][0])){
                    c = j;
                }
            }
            String[] s = Dependencias(a,b,c,VecInstructUnit[id][0]);
            Estacaodereserva_store(s);
        }else if(VecInstructUnit[id][0].equals("DIV")){
            for (int j = 0; j < Registradores.length; j++){
                if (VecInstructUnit[id][1].equals(Registradores[j][0]) && j > 1){
                    a = j;
                }
                if (VecInstructUnit[id][2].equals(Registradores[j][0])){
                    b = j;
                }
                if (VecInstructUnit[id][3].equals(Registradores[j][0])){
                    c = j;
                }
            }
            String[] s = Dependencias(a,b,c,VecInstructUnit[id][0]);
            Estacaodereserva_mult(s);
        }
    }

    // Encontra a proxima instrução
    void find(){
        while ( Pc < VecInstructUnit.length) {
            AtzPorCl();
            Decode(Pc);
            imprimirMatriz(UnidadesFuncionais);
            System.out.printf("Pc: " + Pc + " < " + VecInstructUnit.length);
            System.out.printf("\n");
        }
        Ciclos++;
    }
    void Mips(){
        //
    }
}

public class Main {
    public  static void comand(){
        try {
            // Cria o ProcessBuilder com o comando desejado
            ProcessBuilder processBuilder = new ProcessBuilder("ls", "-a"); // Exemplo: comando 'ls -l' no Linux/Mac
            // Para Windows, use algo como: ProcessBuilder processBuilder = new ProcessBuilder("cmd.exe", "/c", "dir");

            // Redireciona a saída de erro para a saída padrão
            processBuilder.redirectErrorStream(true);

            // Inicia o processo
            Process process = processBuilder.start();

            // Lê a saída do comando
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            // Aguarda o término do processo e verifica o código de saída
            int exitCode = process.waitFor();
            System.out.println("Comando executado com código de saída: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String[] firt(String nomeArquivo) {
        // Usamos ArrayList temporariamente para tamanho dinâmico
        ArrayList<String> linhas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(nomeArquivo))) {
            String linha;
            // Lê cada linha do arquivo
            while ((linha = br.readLine()) != null) {
                linhas.add(linha);
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            return new String[0]; // Retorna array vazio em caso de erro
        }

        // Converte ArrayList para array
        return linhas.toArray(new String[0]);
    }

    public static  String[][] instructLoad(String filePath){
        // Caminho do arquivo de entrada
        //String filePath = "./instruct.luix";

        // Lista para armazenar as instruções
        ArrayList<String> instructions = new ArrayList<>();

        // Ler o arquivo
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    instructions.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            String[][] vazio = new String[1][1];
            vazio[0][0] = "-1";
            return vazio;
        }

        // Criar matriz para armazenar as instruções separadas
        String[][] matrix = new String[instructions.size()][];

        // Processar cada instrução
        for (int i = 0; i < instructions.size(); i++) {
            // Separar por vírgula e remover espaços em branco
            matrix[i] = Arrays.stream(instructions.get(i).split(","))
                    .map(String::trim)
                    .toArray(String[]::new);
        }
        return matrix;
    }


    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.print("Hello and welcome!\n");
        Mpis mpis = new Mpis();
        String[][] vetor = instructLoad("./instruct.luix");

        mpis.load(vetor);
        mpis.find();

    }
}