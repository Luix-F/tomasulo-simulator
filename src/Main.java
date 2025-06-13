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

    public static String[][] cpyMatriz(String[][] matriz){
        String[][] cpy = new String[matriz.length][matriz[0].length];
        for (int i = 0; i < matriz.length; i++) {
            for (int j = 0; j < matriz[i].length; j++) {
                cpy[i][j] = matriz[i][j];
            }
        }
        return cpy;
    }

    public static String[] cpyVec(String[] vec){
        String[] novo = new String[vec.length];
        for (int i = 0; i < vec.length; i++) {
            novo[i] = vec[i];
        }
        return novo;
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

    // Total de 5 janelas de intrução (Estação de Reserva)
    String[][] Estacao_de_Reserva_ALU;
    String[][] Estacao_de_Reserva_mult;
    String[][] Estacao_de_Reserva_br;
    String[][] Estacao_de_Reserva_load;
    String[][] Estacao_de_Reserva_store;

    // Tamanho da janela de intruções
    static int Nji = 4;

    // Buffer de Reordenamento
    String[][] Buffer_De_Reordenamento;


    // Ciclos por instrução
    static int Load = 2;
    static int Store = 1;
    static int Mult = 2;
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
                {"$s6", "vazio"}, {"$s7", "vazio"},


        };
        Registradores = R;

        String RgR[][] = {// Renomeação
                // Nome, valor, referencia
                {"$ra", "vazio", "vazio"}, {"$rb", "vazio", "vazio"},
                {"$rc", "vazio", "vazio"}, {"$rd", "vazio", "vazio"},
                {"$re", "vazio", "vazio"}, {"$rf", "vazio", "vazio"},
        };
        RegRenomeacao = cpyMatriz(RgR);
        Random rand = new Random();
        for (int i = 2; i < Registradores.length; i++) {
            Registradores[i][1] = "" + i;//(rand.nextInt(100) + 1);
        }

        String UF[][] = {
                //UF,  ocupado, Reg,  Reg,    Reg/interge,tempo
                {"ADD", "N", "vazio", "vazio", "vazio", "0", "issue"},
                {"ADD", "N", "vazio", "vazio", "vazio", "0", "issue"},
                {"ADD", "N", "vazio", "vazio", "vazio", "0", "issue"},
                {"MULT", "N", "vazio", "vazio", "vazio", "0", "issue"},
                {"MULT", "N", "vazio", "vazio", "vazio", "0", "issue"},
                {"LD", "N", "vazio", "vazio", "vazio", "0", "issue"},
                {"LD", "N", "vazio", "vazio", "vazio", "0", "issue"},
                {"SW", "N", "vazio", "vazio", "vazio", "0", "issue"},
                {"SW", "N", "vazio", "vazio", "vazio", "0", "issue"}
        };
        UnidadesFuncionais = cpyMatriz(UF);

        {
            String er[][] = new String[Nji][7];
            String l_er[] = {"OP", "ID", "VALOR", "ID", "VALOR", "ID", "issue"};
            for (int i = 0; i < Nji; i++) {
                er[i] = cpyVec(l_er);

            }

            Estacao_de_Reserva_ALU = cpyMatriz(er);
            Estacao_de_Reserva_mult = cpyMatriz(er);
            Estacao_de_Reserva_load = cpyMatriz(er);
            Estacao_de_Reserva_store = cpyMatriz(er);
            Estacao_de_Reserva_br = cpyMatriz(er);
        }

        {
            String bf[][] = new String[5 * Nji][7];
            //            Instrução	   i	   j	    k	TempExec WrResult
            String l_bf[] = {"OP", "vazio", "vazio", "vazio", "TMP", "WR", "issue"};
            for (int i = 0; i < bf.length; i++) {
                for (int j = 0; j < bf[0].length; j++) {
                    bf[i][j] = l_bf[j];
                }

            }
            Buffer_De_Reordenamento = cpyMatriz(bf);
        }
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

    public int searchIssus(String issue){
        for (int i = 0; i < Buffer_De_Reordenamento.length; i++) {
            if (issue.equals(Buffer_De_Reordenamento[i][6])){
                return i;
            }
        }
        return -1;
    }

    // Executa a operação no sistema real
    public int OperacaoReal(String op, int b, int c){
        if (op.equals("ADD")){
            return b + c;
        } else if (op.equals("SUB")) {
            return b - c;
        }else if (op.equals("MULT")) {
            return b * c;
        }else if (op.equals("LD-")) {
            return 9;
        }else if (op.equals("SW-")) {
            return 9;
        }else if (op.equals("DIV")) {
            return b / c;
        }else if (op.equals("ooo")) {
            return b - c;
        }
        return 999999999;
    }

    // Escreve de volta no registrador
    public int WriteBack(String op, String a, String b, String c){
        int r = 0;
        if (a.startsWith("$r")) {
            int arr[] = PosicaoReg(RegRenomeacao[RegRenomeadoPosicao(a)][2],b,c);

            Integer numero = Integer.valueOf(Registradores[arr[1]][1]);
            int bP = numero.intValue();

            numero = Integer.valueOf(Registradores[arr[2]][1]);
            int cP = numero.intValue();
            r = OperacaoReal(op, bP, cP);

            RegRenomeacao[RegRenomeadoPosicao(a)][1] = ""+r;
            //Registradores[arr[0]][1] = "" + r;
        }else {
            int arr[] = PosicaoReg(a,b,c);
            Integer numero = Integer.valueOf(Registradores[arr[1]][1]);
            int bP = numero.intValue();

            numero = Integer.valueOf(Registradores[arr[2]][1]);
            int cP = numero.intValue();
            r = OperacaoReal(op, bP, cP);

            Registradores[arr[0]][1] = "" + r;
        }




        return r;
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
                    // {"ADD", "N", "vazio", "vazio", "vazio", "0", "issue"},

                    Buffer_De_Reordenamento[searchIssus(UnidadesFuncionais[i][6])][4] = "" + Ciclos;
                    Buffer_De_Reordenamento[searchIssus(UnidadesFuncionais[i][6])][5] = ""+WriteBack(Buffer_De_Reordenamento[searchIssus(UnidadesFuncionais[i][6])][0], UnidadesFuncionais[i][2], UnidadesFuncionais[i][3], UnidadesFuncionais[i][4]);

                    UnidadesFuncionais[i][1] = "N";
                    UnidadesFuncionais[i][2] = "vazio";
                    UnidadesFuncionais[i][3] = "vazio";
                    UnidadesFuncionais[i][4] = "vazio";
                    UnidadesFuncionais[i][5] = "0";
                    UnidadesFuncionais[i][6] = "issue";

                }
            }
        }
    }

    // Despacha a instrução para a execução e atualiza a Estação de Reserva
    public int Despacho(){
        if (Pc == 4) {
            System.out.println();
        }
        for (int i = 0; i < Estacao_de_Reserva_ALU.length; i++) {
            if (!(Estacao_de_Reserva_ALU[i][0].equals("OP")) && !(Estacao_de_Reserva_ALU[i][2].equals("VALOR")) && !(Estacao_de_Reserva_ALU[i][4].equals("VALOR")) ){

                if (Exec(Estacao_de_Reserva_ALU[i])){
                    for (int j = i + 1; j < Estacao_de_Reserva_ALU.length; j++) {
                        Estacao_de_Reserva_ALU[j - 1] = Estacao_de_Reserva_ALU[j];
                    }
                    String er[] = {"OP", "ID", "VALOR", "ID", "VALOR", "ID", "issue"};
                    Estacao_de_Reserva_ALU[Estacao_de_Reserva_ALU.length - 1] = er;
                    i--;
                }
            }
        }

        for (int i = 0; i < Estacao_de_Reserva_mult.length; i++) {
            if (!(Estacao_de_Reserva_mult[i][0].equals("OP")) && !(Estacao_de_Reserva_mult[i][2].equals("VALOR")) && !(Estacao_de_Reserva_mult[i][4].equals("VALOR")) ){
                if (Exec(Estacao_de_Reserva_mult[i])) {
                    for (int j = i + 1; j < Estacao_de_Reserva_mult.length; j++) {
                        Estacao_de_Reserva_mult[j - 1] = Estacao_de_Reserva_mult[j];
                    }
                    String er[] = {"OP", "ID", "VALOR", "ID", "VALOR", "ID", "issue"};
                    Estacao_de_Reserva_mult[Estacao_de_Reserva_mult.length - 1] = er;
                    i--;
                }
            }
        }

        for (int i = 0; i < Estacao_de_Reserva_load.length; i++) {
            if (!(Estacao_de_Reserva_load[i][0].equals("OP")) && !(Estacao_de_Reserva_load[i][2].equals("VALOR")) && !(Estacao_de_Reserva_load[i][4].equals("VALOR")) ){
                if (Exec(Estacao_de_Reserva_load[i])) {
                    for (int j = i + 1; j < Estacao_de_Reserva_load.length; j++) {
                        Estacao_de_Reserva_load[j - 1] = Estacao_de_Reserva_load[j];
                    }
                    String er[] = {"OP", "ID", "VALOR", "ID", "VALOR", "ID", "issue"};
                    Estacao_de_Reserva_load[Estacao_de_Reserva_load.length - 1] = er;
                    i--;
                }
            }
        }

        for (int i = 0; i < Estacao_de_Reserva_store.length; i++) {
            if (!(Estacao_de_Reserva_store[i][0].equals("OP")) && !(Estacao_de_Reserva_store[i][2].equals("VALOR")) && !(Estacao_de_Reserva_store[i][4].equals("VALOR")) ){
                if (Exec(Estacao_de_Reserva_store[i])) {
                    for (int j = i + 1; j < Estacao_de_Reserva_store.length; j++) {
                        Estacao_de_Reserva_store[j - 1] = Estacao_de_Reserva_store[j];
                    }
                    String er[] = {"OP", "ID", "VALOR", "ID", "VALOR", "ID", "issue"};
                    Estacao_de_Reserva_store[Estacao_de_Reserva_store.length - 1] = er;
                    i--;
                }
            }
        }

        for (int i = 0; i < Estacao_de_Reserva_br.length; i++) {
            if (!(Estacao_de_Reserva_br[i][0].equals("OP")) && !(Estacao_de_Reserva_br[i][2].equals("VALOR")) && !(Estacao_de_Reserva_br[i][4].equals("VALOR")) ){
                if (Exec(Estacao_de_Reserva_br[i])) {
                    for (int j = i + 1; j < Estacao_de_Reserva_br.length; j++) {
                        Estacao_de_Reserva_br[j - 1] = Estacao_de_Reserva_br[j];
                    }
                    String er[] = {"OP", "ID", "VALOR", "ID", "VALOR", "ID", "issue"};
                    Estacao_de_Reserva_br[Estacao_de_Reserva_br.length - 1] = er;
                    i--;
                }
            }
        }
        return 0;
    }

    // Carrega a tarefa para a execução
    public boolean Exec(String[] instrucao){
        // {"OP", "ID", "VALOR", "ID", "VALOR", "ID", "issue"};
        //   0      1    2        3        4        5    6
        // {"ADD", "N", "vazio", "vazio", "vazio", "0", "issue"},
        int posi[] = PosicaoUF(instrucao[0]);
        for (int i = posi[0]; i <= posi[1]; i++) {
            System.out.printf("UnidadesFuncionais[i][1]:" + UnidadesFuncionais[i][1] + " - " + i + "\n");
            if ((UnidadesFuncionais[i][1].equals("N"))){
                UnidadesFuncionais[i][1] = "Y";

                //UnidadesFuncionais[i][0] = instrucao[0];

                UnidadesFuncionais[i][2] = instrucao[1];
                //Integer numero = Integer.valueOf(instrucao[2]); // Retorna um objeto Integer
                //int valorPrimitivo = numero.intValue(); // Converte para int, se necessário
                UnidadesFuncionais[i][3] = instrucao[2];
                //numero = Integer.valueOf(instrucao[4]);
                //valorPrimitivo = numero.intValue();
                UnidadesFuncionais[i][4] = instrucao[4];
                UnidadesFuncionais[i][6] = instrucao[6];

                return true;
            }
        }
        return false;
    }

    // Ponteiro para os Registradores
    public int[] PosicaoReg(String r0, String r1, String r2){
        if (r0.startsWith("$r")) {

        }
        int a = 0, b = 0, c = 0;
        for (int j = 0; j < Registradores.length; j++) {
            if (r0.equals(Registradores[j][0]) && j > 1) {
                a = j;
            }
            if (r1.equals(Registradores[j][0])) {
                b = j;
            }
            if (r2.equals(Registradores[j][0])) {
                c = j;
            }
        }
        int arr[] = new int[3];
        arr[0] = a;
        arr[1] = b;
        arr[2] = c;
        return arr;
    }

    // Posição do Registrador de Renomeação
    public int RegRenomeadoPosicao(String s) {
        for (int i = 0; i < RegRenomeacao.length; i++) {
            if (RegRenomeacao[i][0].equals(s)){
                return i;
            }
        }
        return 0;
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

    // Carregamento no Buffer de Reordenamento
    public int BufferDeReordenamento(String[] s){
        //   0     1     2        3     4        5      6
        // {"OP", "ID", "VALOR", "ID", "VALOR", "ID", "issue"};
        // {"OP", "vazio", "vazio", "vazio", "TMP", "WR", issue};
        for (int i = 0; i < Buffer_De_Reordenamento.length; i++) {
            if (Buffer_De_Reordenamento[i][0].equals("OP")){
                Buffer_De_Reordenamento[i][0] = s[0];
                Buffer_De_Reordenamento[i][1] = s[1];
                if (s[2].equals("VALOR")){
                    Buffer_De_Reordenamento[i][2] = s[3];
                }else {
                    Buffer_De_Reordenamento[i][2] = s[2];
                }
                if (s[4].equals("VALOR")) {
                    Buffer_De_Reordenamento[i][3] = s[5];
                }else {
                    Buffer_De_Reordenamento[i][3] = s[4];
                }
                Buffer_De_Reordenamento[i][6] = ""+Pc;
                return 0;
            }
        }
        return 0;
    }

    // Estações de Reserva
    public int Estacaodereserva_ALU(String[] s){
        // {"OP", "ID", "VALOR", "ID", "VALOR", "ID", "issue"};
        for (int i = 0; i < Estacao_de_Reserva_ALU.length; i++) {
            if (Estacao_de_Reserva_ALU[i][1].equals("ID")){
                Estacao_de_Reserva_ALU[i] = s;
                BufferDeReordenamento(s);
                Pc++;
                return 0;
            }
        }
        return 0;
    }

    public int Estacaodereserva_mult(String[] s){
        for (int i = 0; i < Estacao_de_Reserva_mult.length; i++) {
            if (Estacao_de_Reserva_mult[i][1].equals("ID")){
                Estacao_de_Reserva_mult[i] = s;
                BufferDeReordenamento(s);
                Pc++;
                return 0;
            }
        }
        return 0;
    }

    public int Estacaodereserva_load(String[] s){
        for (int i = 0; i < Estacao_de_Reserva_load.length; i++) {
            if (Estacao_de_Reserva_load[i][1].equals("ID")){
                Estacao_de_Reserva_load[i] = s;
                BufferDeReordenamento(s);
                Pc++;
                return 0;
            }
        }
        return 0;
    }

    public int Estacaodereserva_store(String[] s){
        for (int i = 0; i < Estacao_de_Reserva_store.length; i++) {
            if (Estacao_de_Reserva_store[i][1].equals("ID")){
                Estacao_de_Reserva_store[i] = s;
                BufferDeReordenamento(s);
                Pc++;
                return 0;
            }
        }
        return 0;
    }

    public int Estacaodereserva_br(String[] s){
        for (int i = 0; i < Estacao_de_Reserva_br.length; i++) {
            if (Estacao_de_Reserva_br[i][1].equals("ID")){
                Estacao_de_Reserva_br[i] = s;
                BufferDeReordenamento(s);
                Pc++;
                return 0;
            }
        }
        return 0;
    }

    // Encontra registrador de renomeação livre
    public int Renomeacao(){
        for (int i = 0; i < RegRenomeacao.length; i++) {
            if (RegRenomeacao[i][2].equals("vazio")){
                return i;
            }
        }
        return 0;
    }


    // Atualiza os valores de dependencias
    public int AtzD(){
        //AtzRenomeado();
        for (int i = 0; i < Estacao_de_Reserva_ALU.length; i++) {
            if (!Estacao_de_Reserva_ALU[i][0].equals("OP")){
                String ER[] = {"OP", "ID", "VALOR", "ID", "VALOR", "ID", "issue"};
                ER = cpyVec(Estacao_de_Reserva_ALU[i]);
                if (!ER[3].equals("ID")) {
                    ER[2] = ER[3];
                    ER[3] = "ID";
                }
                if (!ER[5].equals("ID")) {
                    ER[4] = ER[5];
                    ER[5] = "ID";
                }
                if (true) {
                    if (Estacao_de_Reserva_ALU[i][3].equals("ID") || Estacao_de_Reserva_ALU[i][5].equals("ID")) {
                        Integer numero = Integer.valueOf(ER[6]);
                        int valr = numero.intValue();
                        String[][] s = Dependencias(ER[1], ER[2], ER[4], ER[0], valr);
                        ER[7] = s[0][7];
                        if (s[1][1].equals("1")) {
                            ER[3] = ER[2];
                            ER[2] = "VALOR";
                        }
                        if (s[1][2].equals("1")) {
                            ER[5] = ER[4];
                            ER[4] = "VALOR";
                        }
                        if (s[1][0].equals("1")){
                            ER[1] = s[0][1];
                        }/*else {
                            boolean b = false;
                            for (String[] strings : RegRenomeacao) {
                                if (strings[2].equals(ER[1])) {
                                    b = true;
                                    break;
                                }
                            }

                            if (b){
                                int arr[] = PosicaoReg(RegRenomeacao[RegRenomeadoPosicao(Buffer_De_Reordenamento[i][1])][2], "$0", "$0");
                                Registradores[arr[0]][1] = RegRenomeacao[RegRenomeadoPosicao(Buffer_De_Reordenamento[i][1])][1];
                                //Buffer_De_Reordenamento[i][1] = Registradores[arr[0]][0];
                                RegRenomeacao[RegRenomeadoPosicao(Buffer_De_Reordenamento[i][1])][1] = "vazio";
                                RegRenomeacao[RegRenomeadoPosicao(Buffer_De_Reordenamento[i][1])][2] = "vazio";
                            }

                        }*/
                        ER[1] = s[0][1];
                        Estacao_de_Reserva_ALU[i] = cpyVec(ER);
                    }
                } else {
                    //Estacao_de_Reserva_ALU[i] = cpyVec(ER);
                }
            }
        }
        for (int i = 0; i < Estacao_de_Reserva_mult.length; i++) {
            if (!Estacao_de_Reserva_mult[i][0].equals("OP")){
                String ER[] = {"OP", "ID", "VALOR", "ID", "VALOR", "ID", "issue"};
                ER = cpyVec(Estacao_de_Reserva_mult[i]);
                if (!ER[3].equals("ID")) {
                    ER[2] = ER[3];
                    ER[3] = "ID";
                }
                if (!ER[5].equals("ID")) {
                    ER[4] = ER[5];
                    ER[5] = "ID";
                }

                if (Estacao_de_Reserva_mult[i][3].equals("ID") || Estacao_de_Reserva_mult[i][5].equals("ID")) {
                    Integer numero = Integer.valueOf(ER[6]);
                    int valr = numero.intValue();
                    String[][] s = Dependencias(ER[1], ER[2], ER[4], ER[0], valr);
                    if (s[1][1].equals("1")) {
                        ER[3] = ER[2];
                        ER[2] = "VALOR";
                    }
                    if (s[1][2].equals("1")) {
                        ER[5] = ER[4];
                        ER[4] = "VALOR";
                    }
                    Estacao_de_Reserva_mult[i] = cpyVec(ER);
                }

            }
        }
        for (int i = 0; i < Estacao_de_Reserva_load.length; i++) {
            if (!Estacao_de_Reserva_load[i][0].equals("OP")){
                String ER[] = {"OP", "ID", "VALOR", "ID", "VALOR", "ID", "issue"};
                ER = cpyVec(Estacao_de_Reserva_load[i]);
                if (!ER[3].equals("ID")) {
                    ER[2] = ER[3];
                    ER[3] = "ID";
                }
                if (!ER[5].equals("ID")) {
                    ER[4] = ER[5];
                    ER[5] = "ID";
                }

                if (Estacao_de_Reserva_load[i][3].equals("ID") || Estacao_de_Reserva_load[i][5].equals("ID")) {
                    Integer numero = Integer.valueOf(ER[6]);
                    int valr = numero.intValue();
                    String[][] s = Dependencias(ER[1], ER[2], ER[4], ER[0], valr);
                    if (s[1][1].equals("1")) {
                        ER[3] = ER[2];
                        ER[2] = "VALOR";
                    }
                    if (s[1][2].equals("1")) {
                        ER[5] = ER[4];
                        ER[4] = "VALOR";
                    }
                    Estacao_de_Reserva_load[i] = cpyVec(ER);
                }

            }
        }
        for (int i = 0; i < Estacao_de_Reserva_store.length; i++) {
            if (!Estacao_de_Reserva_store[i][0].equals("OP")){
                String ER[] = {"OP", "ID", "VALOR", "ID", "VALOR", "ID", "issue"};
                ER = cpyVec(Estacao_de_Reserva_store[i]);
                if (!ER[3].equals("ID")) {
                    ER[2] = ER[3];
                    ER[3] = "ID";
                }
                if (!ER[5].equals("ID")) {
                    ER[4] = ER[5];
                    ER[5] = "ID";
                }

                if (Estacao_de_Reserva_store[i][3].equals("ID") || Estacao_de_Reserva_store[i][5].equals("ID")) {
                    Integer numero = Integer.valueOf(ER[6]);
                    int valr = numero.intValue();
                    String[][] s = Dependencias(ER[1], ER[2], ER[4], ER[0], valr);
                    if (s[1][1].equals("1")) {
                        ER[3] = ER[2];
                        ER[2] = "VALOR";
                    }
                    if (s[1][2].equals("1")) {
                        ER[5] = ER[4];
                        ER[4] = "VALOR";
                    }
                    Estacao_de_Reserva_store[i] = cpyVec(ER);
                }

            }
        }
        for (int i = 0; i < Estacao_de_Reserva_br.length; i++) {
            if (!Estacao_de_Reserva_br[i][0].equals("OP")){
                String ER[] = {"OP", "ID", "VALOR", "ID", "VALOR", "ID", "issue"};
                ER = cpyVec(Estacao_de_Reserva_br[i]);
                if (!ER[3].equals("ID")) {
                    ER[2] = ER[3];
                    ER[3] = "ID";
                }
                if (!ER[5].equals("ID")) {
                    ER[4] = ER[5];
                    ER[5] = "ID";
                }

                if (Estacao_de_Reserva_br[i][3].equals("ID") || Estacao_de_Reserva_br[i][5].equals("ID")) {
                    Integer numero = Integer.valueOf(ER[6]);
                    int valr = numero.intValue();
                    String[][] s = Dependencias(ER[1], ER[2], ER[4], ER[0], valr);
                    if (s[1][1].equals("1")) {
                        ER[3] = ER[2];
                        ER[2] = "VALOR";
                    }
                    if (s[1][2].equals("1")) {
                        ER[5] = ER[4];
                        ER[4] = "VALOR";
                    }
                    Estacao_de_Reserva_br[i] = cpyVec(ER);
                }

            }
        }
        return 0;
    }



    // Indendifica dependencias
    public String[][] Dependencias(String r0, String r1, String r2, String op, int posi){
        if (Pc == 4){
            System.out.println();
        }
        //   0     1     2        3     4        5      6
        // {"OP", "ID", "VALOR", "ID", "VALOR", "ID", "issue"};
        String ER[][] = {
                {"OP", "ID", "VALOR", "ID", "VALOR", "ID", "issue"},
                {"-", "-", "-", "+", "+", "+", "+"},
        };
        ER[0][2] = r1;//Registradores[r1][0];
        ER[0][4] = r2;//Registradores[r2][0];
        ER[0][1] = r0;//Registradores[r0][0];

        for (int i = 0; i < Buffer_De_Reordenamento.length && !Buffer_De_Reordenamento[i][6].equals(""+posi); i++) {
            if (Buffer_De_Reordenamento[i][4].equals("TMP")){
                String s1 = r1;//Registradores[r1][0];
                String s2 = r2;//Registradores[r2][0];
                String s0 = r0;//Registradores[r0][0];

                String b0 = "";
                if (Buffer_De_Reordenamento[i][1].startsWith("$r")) {
                    int arr[] = PosicaoReg(RegRenomeacao[RegRenomeadoPosicao(Buffer_De_Reordenamento[i][1])][2], "$0", "$0");
                    b0 = Registradores[arr[0]][0];
                }else {
                    b0 = s0;
                }



                ER[0][0] = op;
                //ER[1] = ""+r0;
                // {"OP", "vazio", "vazio", "vazio", "TMP", "WR", "issue"};
                //if (Estacao_de_Reserva_ALU[i][1].equals(s1) || Estacao_de_Reserva_mult[i][1].equals(s1) || Estacao_de_Reserva_load[i][1].equals(s1) || Estacao_de_Reserva_store[i][1].equals(s1) || Estacao_de_Reserva_br[i][1].equals(s1)) { //
                if (s1.equals(Buffer_De_Reordenamento[i][1])){
                    if (!r1.equals("VALOR") && !r1.equals("ID")){
                        ER[0][3] = r1;
                        ER[0][2] = "VALOR";
                        ER[1][1] = "1";
                    }
                }else if (Buffer_De_Reordenamento[i][1].startsWith("$r")) {
                    int arr[] = PosicaoReg(RegRenomeacao[RegRenomeadoPosicao(Buffer_De_Reordenamento[i][1])][2], "$0", "$0");
                    if (s1.equals(Buffer_De_Reordenamento[arr[0]][1])){
                        if (!r1.equals("VALOR") && !r1.equals("ID")){
                            ER[0][3] = r1;
                            ER[0][2] = "VALOR";
                            ER[1][1] = "1";
                        }
                    }
                }
                //if (Estacao_de_Reserva_ALU[i][1].equals(s2) || Estacao_de_Reserva_mult[i][1].equals(s2) || Estacao_de_Reserva_load[i][1].equals(s2) || Estacao_de_Reserva_store[i][1].equals(s2) || Estacao_de_Reserva_br[i][1].equals(s2)) {
                if (s2.equals(Buffer_De_Reordenamento[i][1])){
                    if (!r2.equals("VALOR") && !r2.equals("ID")){
                        ER[0][5] = r2;
                        ER[0][4] = "VALOR";
                        ER[1][2] = "1";
                    }
                }
                /*if (Estacao_de_Reserva_ALU[i][2].equals(s0) || Estacao_de_Reserva_ALU[i][4].equals(s0) || Estacao_de_Reserva_ALU[i][3].equals(s0) || Estacao_de_Reserva_ALU[i][5].equals(s0)
                        || Estacao_de_Reserva_mult[i][2].equals(s0) || Estacao_de_Reserva_mult[i][4].equals(s0) || Estacao_de_Reserva_mult[i][3].equals(s0) || Estacao_de_Reserva_mult[i][5].equals(s0)
                        || Estacao_de_Reserva_load[i][2].equals(s0) || Estacao_de_Reserva_load[i][4].equals(s0) || Estacao_de_Reserva_load[i][3].equals(s0) || Estacao_de_Reserva_load[i][5].equals(s0)
                        || Estacao_de_Reserva_store[i][2].equals(s0) || Estacao_de_Reserva_store[i][4].equals(s0) || Estacao_de_Reserva_store[i][3].equals(s0) || Estacao_de_Reserva_store[i][5].equals(s0)
                        || Estacao_de_Reserva_br[i][2].equals(s0) || Estacao_de_Reserva_br[i][4].equals(s0) || Estacao_de_Reserva_br[i][3].equals(s0) || Estacao_de_Reserva_br[i][5].equals(s0)
                ) { */// ant-dependência
                if (b0.equals(Buffer_De_Reordenamento[i][2]) || b0.equals(Buffer_De_Reordenamento[i][4]) || b0.equals(Buffer_De_Reordenamento[i][3]) || b0.equals(Buffer_De_Reordenamento[i][5])){
                    //boolean b = false;
                    ER[1][0] = "1";
                    int rgr = Renomeacao();
                    ER[0][1] = RegRenomeacao[rgr][0];
                    RegRenomeacao[rgr][2] = r0; // referencia real
                    /*
                    for (String[] strings : RegRenomeacao) {
                        if (strings[2].equals(r0)) {
                            b = true;
                            break;
                        }
                    }
                    if (!b){
                        int rgr = Renomeacao();
                        ER[0][1] = RegRenomeacao[rgr][0];
                        RegRenomeacao[rgr][2] = r0; // referencia real
                    }else {
                        int rgr = Renomeacao();
                        ER[0][1] = RegRenomeacao[rgr][0];
                        RegRenomeacao[rgr][2] = r0; // referencia real
                    }*/
                }/*
                if (Buffer_De_Reordenamento[i][1].startsWith("$r")) {
                    int arr[] = PosicaoReg(RegRenomeacao[RegRenomeadoPosicao(Buffer_De_Reordenamento[i][1])][2], "$0", "$0");

                    if (b0.equals(Registradores[arr[0]][0])){
                        ER[0][7] = "N";
                    }
                }*/
            }
        }
        ER[0][6] = ""+Pc;
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

            String[][] s = Dependencias(Registradores[a][0],Registradores[b][0],Registradores[c][0],VecInstructUnit[id][0], Buffer_De_Reordenamento.length);
            Estacaodereserva_ALU(s[0]);
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
            String[][] s = Dependencias(Registradores[a][0],Registradores[b][0],Registradores[c][0],VecInstructUnit[id][0], Buffer_De_Reordenamento.length);
            Estacaodereserva_ALU(s[0]);
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
            String[][] s = Dependencias(Registradores[a][0],Registradores[b][0],Registradores[c][0],VecInstructUnit[id][0], Buffer_De_Reordenamento.length);
            Estacaodereserva_mult(s[0]);
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
            String[][] s = Dependencias(Registradores[a][0],Registradores[b][0],Registradores[c][0],VecInstructUnit[id][0], Buffer_De_Reordenamento.length);
            Estacaodereserva_load(s[0]);
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
            String[][] s = Dependencias(Registradores[a][0],Registradores[b][0],Registradores[c][0],VecInstructUnit[id][0], Buffer_De_Reordenamento.length);
            Estacaodereserva_store(s[0]);
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
            String[][] s = Dependencias(Registradores[a][0],Registradores[b][0],Registradores[c][0],VecInstructUnit[id][0], Buffer_De_Reordenamento.length);
            Estacaodereserva_mult(s[0]);
        }else if(VecInstructUnit[id][0].equals("BEQ")){/// //////////////////////////////
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
            String[][] s = Dependencias(Registradores[a][0],Registradores[b][0],Registradores[c][0],VecInstructUnit[id][0], Buffer_De_Reordenamento.length);
            Estacaodereserva_br(s[0]);
        }
    }

    // Reescreve 'r' no registrador original
    public int WritRa(){
        for (int i = 0; i < Buffer_De_Reordenamento.length; i++) {
            if (Buffer_De_Reordenamento[i][1].startsWith("$r")) {
                if (!Buffer_De_Reordenamento[i][4].equals("TMP")){
                    int arr[] = PosicaoReg(RegRenomeacao[RegRenomeadoPosicao(Buffer_De_Reordenamento[i][1])][2], "$0", "$0");
                    Registradores[arr[0]][1] = RegRenomeacao[RegRenomeadoPosicao(Buffer_De_Reordenamento[i][1])][1];
                    RegRenomeacao[RegRenomeadoPosicao(Buffer_De_Reordenamento[i][1])][1] = "vazio";
                    RegRenomeacao[RegRenomeadoPosicao(Buffer_De_Reordenamento[i][1])][2] = "vazio";
                }else {
                    return 0;
                }

            }
        }
        return 0;
    }

    // Encontra a proxima instrução
    void find(){
        while ( Buffer_De_Reordenamento[VecInstructUnit.length-1][5].equals("WR")) {
            System.out.printf("\n\n\n\n");
            AtzPorCl();
            AtzD();
            WritRa();
            if (Pc < VecInstructUnit.length){Decode(Pc);} // Repita para mais de uma instrucao
            Despacho();
            System.out.println("UnidadesFuncionais: ");
            imprimirMatriz(UnidadesFuncionais);
            System.out.println();
            System.out.println("Buffer_De_Reordenamento: ");
            imprimirMatriz(Buffer_De_Reordenamento);
            System.out.println();
            System.out.println("Estacao_de_Reserva_ALU: ");
            imprimirMatriz(Estacao_de_Reserva_ALU);
            System.out.printf("Pc: " + Pc + " < " + VecInstructUnit.length);
            System.out.println();
            Ciclos++;
        }

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