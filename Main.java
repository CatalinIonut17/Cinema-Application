import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/examenjavap";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) throws IOException, SQLException {


        Scanner scanner = new Scanner(System.in);

        String formatInFrontOf = String.format("%7s", "");
        Cinema cinema = new Cinema();
        Sala salaCinema = new Sala();
        CapacitateCinema capacitateCinema = new CapacitateCinema();


        try (Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
             Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE)) {

            System.out.println("Buna ziua! " + "Folositi aceasta aplicatie ca:\n" +
                    formatInFrontOf
                    + "Utilizator  sau  PersonalCinema\n");

            System.out.println("Daca se doreste inchiderea programului tastati:\n"
                    +formatInFrontOf + "Exit\n");

            String scan = scanner.next();

            switch (scan) {
                case "Utilizator":

                    System.out.println("Sunteti in meniul pentru Utilizator.\n"
                            + formatInFrontOf +
                            "Selectati una din operatiile : | SignIn | sau | LogIn |");

                    String comenziUtilizator = scanner.next();

                    if (comenziUtilizator.equals("SignIn")) {

                        System.out.println("Introduceti email-ul:");
                        String username = scanner.next();

                        if (username == null || username.isEmpty()) {
                            System.out.println("Email-ul acesta nu este corect.");
                        }

                        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." +
                                "[a-zA-Z0-9_+&*-]+)*@" +
                                "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

                        Pattern patternEmail = Pattern.compile(emailRegex);

                        if (patternEmail.matcher(username).matches()) {
                            String selectAllUseri = "SELECT * FROM useri WHERE username='" + username + "'";
                            ResultSet resultSetAllUser = statement.executeQuery(selectAllUseri);

                            if (resultSetAllUser.next()) {
                                System.out.println("Email-ul exista deja in baza de date.\n"
                                        + String.format("%5s", "")
                                        + "Te rog sa te loghezi in cont.");
                            } else {
                                System.out.println("Creati o parola: \n" +
                                        formatInFrontOf +
                                        "Parola trebuie sa contina: \n" +
                                        "*minim 8 caractere, \n" +
                                        "*minim o litera mare, \n" +
                                        "*minim o litera mica, \n" +
                                        "*minim o cifra (0-9), \n" +
                                        "*minim un caracter special (@,#,$,%,etc).");

                                String password = scanner.next();

                                String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).{8,20}$";
                                Pattern patternPassword = Pattern.compile(passwordRegex);

                                if (patternPassword.matcher(password).matches()) {
                                    String insertAUser = "INSERT INTO useri VALUES (null, '" + username + "' , '" + password + "')";
                                    statement.executeUpdate(insertAUser);
                                    System.out.println("Userul a fost creat! Va puteti loga in cont.");

                                } else {
                                    System.out.println("Parola nu este corecta, aceasta trebuie sa contina: \n" +
                                            String.format("%5s", "") +
                                            "*minim 8 caractere, \n" +
                                            "*minim o litera mare, \n" +
                                            "*minim o litera mica, \n" +
                                            "*minim o cifra, \n" +
                                            "*minim un caracter special.");
                                }

                            }

                        } else {
                            System.out.println("Va rog sa introduceti un email valid.");
                        }

                    }

                    if (comenziUtilizator.equals("LogIn")) {

                        System.out.println("Email-ul este:");
                        String username = scanner.next();

                        System.out.println("Introduceti parola:");
                        String password = scanner.next();

                        String verifyUser = "SELECT * FROM useri WHERE username='" + username + "'" + "and password='" + password + "'";
                        ResultSet resultSet = statement.executeQuery(verifyUser);


                        if (resultSet.next()) {
                            System.out.println("Te-ai logat cu succes.");

                            String idUserLogat = resultSet.getString("id");

                            System.out.println("Se pot efectua urmatoarele actiuni pentru rezervare: \n" +
                                    formatInFrontOf +
                                    " | Creare | ; | Afisare | ; | Stergere | ");

                            String userReservation = scanner.next();


                            switch (userReservation) {
                                case "Creare":

                                    System.out.println("\n" + "Alege filmul dorit din lista de mai jos:\n");
                                    cinema.afisareFilme();

                                    String filmCU = scanner.next();

                                    if (cinema.filme.contains(filmCU)) {

                                        System.out.println("Cinematograful are 9 sali disponibile, va rog sa alegeti numarul salii: ");
                                        int salaCU = Integer.parseInt(scanner.next());

                                        if (Arrays.stream(salaCinema.salaCinematograf).anyMatch(el -> el == salaCU)) {

                                            System.out.println("Numele pentru care se doreste efectuarea rezervarii este: ");
                                            String numeRezervareCU = scanner.next();

                                            DateTimeFormatter dateTimeFormatterCU = DateTimeFormatter.ofPattern("yyyy/MM/dd");
                                            System.out.println("In ce data doriti rezervarea?\n " +
                                                    formatInFrontOf + " Va rog sa respectati formatul: An/Luna/Zi.");
                                            LocalDate localDateCU = LocalDate.parse(scanner.next(), dateTimeFormatterCU);
                                            LocalDate nowCU = LocalDate.now();

                                            if (localDateCU.isEqual(nowCU) || localDateCU.isAfter(nowCU)) {

                                                String countElement = "SELECT COUNT(*) from rezervariuseri WHERE sala = '" + salaCU + "'" + "and data= '" + localDateCU + "'";
                                                ResultSet resultSetCountCU = statement.executeQuery(countElement);

                                                if (resultSetCountCU.next()) {
                                                    int countCU = resultSetCountCU.getInt(1);

                                                    if (countCU < 20) {
                                                        String insertReservation = "INSERT INTO rezervariuseri VALUES (null,'" + idUserLogat + "' , '" + numeRezervareCU + "' , '" + salaCU + "' , '" + filmCU + "' , '" + localDateCU + "')";
                                                        statement.executeUpdate(insertReservation);
                                                        System.out.println("Ati efectuat o rezervare pe numele " + numeRezervareCU + ", la filmul " + filmCU + ", pentru date de " + localDateCU);
                                                    } else {
                                                        System.out.println("Sala este plina!");
                                                    }
                                                }
                                            } else {

                                                System.out.println("Data introdusa este anterioara zilei de astazi.\n" +
                                                        formatInFrontOf + "Te rog sa alegi o data incepand cu astazi.");
                                            }
                                        } else {
                                            System.out.println("Numarul salii selectate nu exista./n" +
                                                    formatInFrontOf + "Te rog sa alegi sala conform numerelor noastre.");
                                        }
                                    } else {
                                        System.out.println("Filmul nu se afla in lista.\n" +
                                                formatInFrontOf + "Te rog sa selectezi un film din lista. ");
                                    }

                                    break;

                                case "Afisare":

                                    String allDataPrint = "SELECT * FROM rezervariuseri WHERE idUser='" + idUserLogat + "'";
                                    ResultSet resultSetPrint = statement.executeQuery(allDataPrint);

                                    if (getSize2(resultSetPrint) > 0) {

                                        System.out.println("Numele Rezevarii\t\tSala\t\tFilmul\t\tData");

                                        ResultSet resultSetPrint2 = statement.executeQuery(allDataPrint);
                                        while (resultSetPrint2.next()) {
                                            String numeRezevareAfisare = resultSetPrint2.getString("numeRezervare");
                                            int salaAfisare = resultSetPrint2.getInt("sala");
                                            String filmAfisare = resultSetPrint2.getString("film");
                                            Date dataAfisare = resultSetPrint2.getDate("data");
                                            System.out.println(numeRezevareAfisare + "\t\t\t\t" + salaAfisare + "\t\t\t" + filmAfisare + "\t\t" + dataAfisare);
                                        }
                                    } else {
                                        System.out.println("Nu exista rezervari in sistem.");
                                    }

                                    break;

                                case "Stergere":

                                    String selectDataDelete = "SELECT * FROM rezervariuseri WHERE idUser='" + idUserLogat + "'";
                                    ResultSet resultSetDelete = statement.executeQuery(selectDataDelete);

                                    if (getSize2(resultSetDelete) > 0) {
                                        System.out.println("Introduceti numele rezevarii pe care doriti sa o stergeti.:");
                                        String numeRezervareDelete = scanner.next();
                                        String selectDataDelete2 = "SELECT * FROM rezervariuseri WHERE numeRezervare = '" + numeRezervareDelete + "'";
                                        ResultSet resultSetDelete2 = statement.executeQuery(selectDataDelete2);

                                        if (resultSetDelete2.next()) {

                                            String idUserDelete = resultSetDelete2.getString("idUser");

                                            if (idUserDelete.equals(idUserLogat)) {
                                                String deleteDate = "DELETE FROM rezervariuseri WHERE numeRezervare = '" + numeRezervareDelete + "'";
                                                statement.executeUpdate(deleteDate);
                                                System.out.println("Rezervarea " + numeRezervareDelete + " a fost stearsa.");

                                            } else {
                                                System.out.println(String.format("Rezervarea %s nu exista in sistem.", numeRezervareDelete));
                                            }
                                        }
                                    } else {
                                        System.out.println("Nu exista rezervari in sistem.");
                                    }

                                    break;
                            }

                        } else {
                            System.out.println("Username sau parola gresita!\n" +
                                    "Te rog sa incerci din nou.");
                        }
                    }

                    return;

                case "PersonalCinema":
                    System.out.println("Sunteti in meniu: Personal cinematograf.");

                    System.out.println("User-ul este: ");
                    String usernamePersonal = scanner.next();

                    System.out.println("Introduceti parola:");
                    String passwordPersonal = scanner.next();

                    String selectAllUseri = "SELECT * FROM personalcinematograf WHERE username='" + usernamePersonal + "'" + "and password='" + passwordPersonal + "'";
                    ResultSet resultSet = statement.executeQuery(selectAllUseri);

                    if (resultSet.next()) {
                        System.out.println("Te-ai logat cu succes.");

                        System.out.println("Se pot efectua urmatoarele actiuni selectand una din prescurtarile: \n" +
                                formatInFrontOf +
                                " *VerificareCapacitate  - VC\n" + formatInFrontOf +
                                " *StergeRezervare       - SR\n" + formatInFrontOf +
                                " *StergeUtilizator      - SU\n" + formatInFrontOf +
                                " *VerificareRezervare  - VR\n" + formatInFrontOf +
                                " *SalvareDB             - SDB");

                        String comenziPersonal = scanner.next();

                        switch (comenziPersonal) {
                            case "VC":

                                System.out.println("Numarul salii este:");
                                int numarSalaVC = Integer.parseInt(scanner.next());
                                if (Arrays.stream(salaCinema.salaCinematograf).anyMatch(ns -> ns == numarSalaVC)) {
                                    String selectDataVC = "SELECT * FROM rezervariuseri WHERE sala = '" + numarSalaVC + "'";
                                    ResultSet resultSetVC = statement.executeQuery(selectDataVC);
                                    List<String> sala = new ArrayList<>();

                                    while (resultSetVC.next()) {
                                        String userIdVC = resultSetVC.getString("idUser");
                                        sala.add(userIdVC);
                                    }

                                    if (sala.size() > 0) {
                                        System.out.println(String.format("In sala numarul %s au efectuat rezervari urmatorii useri: ", numarSalaVC));
                                        for (int i = 0; i < sala.size(); i++) {
                                            String salaVC= sala.get(i);

                                            String selectUserVC = "SELECT * FROM useri WHERE id='" + salaVC + "'";
                                            ResultSet resultSetSelectUserVC = statement.executeQuery(selectUserVC);
                                            while (resultSetSelectUserVC.next()) {
                                                String usernameVC = resultSetSelectUserVC.getString("username");
                                                System.out.println(formatInFrontOf + "* " + usernameVC);
                                            }
                                        }
                                    } else {
                                        System.out.println(String.format("In sala numarul %s nu exista rezervari efectuate", numarSalaVC));
                                    }

                                    int locuriOcupate = sala.size();
                                    int locuriLibere = capacitateCinema.nrPersoane - locuriOcupate;
                                    System.out.println(String.format("In sala numarul %s mai sunt disponible %s locuri. ", numarSalaVC, locuriLibere));

                                } else {
                                    System.out.println(String.format("Sala numarul %s nu exista.", numarSalaVC));
                                }

                                break;

                            case "SR":

                                System.out.println("Introduceti Userul a carui rezervare doriti sa fie stearsa: ");
                                String deleteUserSR = scanner.next();

                                String selectUserRezervationSR = "SELECT * FROM useri WHERE username ='" + deleteUserSR + "'";
                                ResultSet resultSetUserRezervationSR = statement.executeQuery(selectUserRezervationSR);

                                if (getSize(resultSetUserRezervationSR) > 0) {
                                    String idSR = resultSetUserRezervationSR.getString("id");

                                    System.out.println("Introduceti numele rezervarii pe care doriti sa o stergeti: ");
                                    String numeRezervareSR = scanner.next();

                                    String selectForDeleteSR = "SELECT * FROM rezervariuseri  WHERE idUser ='" + idSR + "'" + "and numeRezervare= '" + numeRezervareSR + "'";
                                    ResultSet resultSetForDeleteSR = statement.executeQuery(selectForDeleteSR);

                                    if (resultSetForDeleteSR.next()) {

                                        String deleteRezervareSR = "DELETE  FROM rezervariuseri WHERE idUser = '" + idSR + "'" + "and numeRezervare='" + numeRezervareSR + "'";
                                        statement.executeUpdate(deleteRezervareSR);
                                        System.out.println("Rezervarea " + numeRezervareSR + " a fost stearsa");

                                    } else {
                                        System.out.println("Rezervarea " + numeRezervareSR + " nu exista");
                                    }
                                } else {
                                    System.out.println("Userul " + deleteUserSR + " nu exista");
                                }

                                break;

                            case "SU":

                                System.out.println("Introduceti userul pe care doriti sa il stergeti: ");
                                String deleteUserSU = scanner.next();

                                String selectUserDeleteSU = "SELECT * FROM useri WHERE username ='" + deleteUserSU + "'";
                                ResultSet resultSetUserDeleteSU = statement.executeQuery(selectUserDeleteSU);

                                if (getSize(resultSetUserDeleteSU) > 0) {
                                    String idSU = resultSetUserDeleteSU.getString("id");

                                    String selectVerifyDeleteSU = "SELECT * FROM rezervariuseri WHERE iduser = '" + idSU + "'";
                                    ResultSet resultSetVerifyDeleteSU = statement.executeQuery(selectVerifyDeleteSU);

                                    if (resultSetVerifyDeleteSU.next()) {
                                        String deleteRezervationSU = "DELETE  FROM rezervariuseri WHERE idUser = '" + idSU + "'";
                                        statement.executeUpdate(deleteRezervationSU);

                                        String deleteUserSQLSU = "DELETE FROM useri WHERE username ='" + deleteUserSU + "'";
                                        statement.executeUpdate(deleteUserSQLSU);

                                        System.out.println("Userul " + deleteUserSU + " si rezervarile acestui user au fost sterse.");

                                    } else {
                                        System.out.println("Nu exista o rezervare.");
                                    }
                                } else {
                                    System.out.println("Userul " + deleteUserSU + " nu exista.");
                                }

                                break;


                            case "VR":
                                System.out.println("Introduceti numele userului: ");
                                String userVerifyVR = scanner.next();

                                String selectUserVerifyVR = "SELECT * FROM useri WHERE username ='" + userVerifyVR + "'";
                                ResultSet resultSetUserVerifyVR = statement.executeQuery(selectUserVerifyVR);

                                if (resultSetUserVerifyVR.next()) {

                                    String idUserVR = resultSetUserVerifyVR.getString("id");

                                    String selectVerifyVR = "SELECT * FROM rezervariuseri WHERE iduser = '" + idUserVR + "'";
                                    ResultSet resultSetSelectVerifyVR = statement.executeQuery(selectVerifyVR);

                                    if (resultSetSelectVerifyVR.next()) {
                                        System.out.println("Numele Rezevarii\t\tSala\t\tFilmul\t\t\tData");

                                        ResultSet resultSetSelectVerifyVR2 = statement.executeQuery(selectVerifyVR);

                                        while (resultSetSelectVerifyVR2.next()) {
                                            String numeRezevareVR = resultSetSelectVerifyVR2.getString("numeRezervare");
                                            int salaVR = resultSetSelectVerifyVR2.getInt("sala");
                                            String filmVR = resultSetSelectVerifyVR2.getString("film");
                                            Date dataVR = resultSetSelectVerifyVR2.getDate("data");

                                            System.out.println(numeRezevareVR + "\t\t\t\t" + salaVR + "\t\t\t" + filmVR + "\t\t" + dataVR);
                                        }
                                    } else {
                                        System.out.println("Nu exista rezervari pentru userul: " + userVerifyVR);
                                    }

                                } else {
                                    System.out.println("Nu exista userul " + userVerifyVR + " in baza de date.");
                                }


                                break;

                            case "SDB":

                                File file = new File("resource/SalvareDb.txt");
                                if (!file.exists()) {
                                    file.createNewFile();
                                }

                                LocalDateTime now = LocalDateTime.now();
                                DateTimeFormatter dateTimeFormatterSDB = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                                String formatOk = now.format(dateTimeFormatterSDB);

                                try (
                                        FileWriter fileWriter = new FileWriter(file);
                                        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

                                    bufferedWriter.write("Salvare: " + formatOk + "\n");
                                    bufferedWriter.newLine();

                                    String idSDB, usernameSDB = null, parolaSDB = null;

                                    String selectAllSDB = "SELECT * FROM useri";
                                    ResultSet resultSetSelectAllSDB = statement.executeQuery(selectAllSDB);
                                    List<String> idListSDB = new ArrayList<>();
                                    List<List<String>> listOfElements = new ArrayList<>();

                                    while (resultSetSelectAllSDB.next()) {
                                        idSDB = resultSetSelectAllSDB.getString("id");
                                        usernameSDB = resultSetSelectAllSDB.getString("username");
                                        parolaSDB = resultSetSelectAllSDB.getString("password");

                                        listOfElements.add(List.of(idSDB, usernameSDB, parolaSDB));

                                    }

                                    if (listOfElements.size() > 0) {
                                        for (int i = 0; i < listOfElements.size(); i++) {
                                            String id = listOfElements.get(i).get(0);
                                            String selectALLRezervari = "SELECT * FROM rezervariuseri WHERE idUser= '" + id + "'";
                                            ResultSet resultSetselectALLRezervari = statement.executeQuery(selectALLRezervari);
                                            while (resultSetselectALLRezervari.next()) {
                                                int idRezervareSDB = resultSetselectALLRezervari.getInt("id");
                                                String numeRezevareSDB = resultSetselectALLRezervari.getString("numeRezervare");
                                                int salaSDB = resultSetselectALLRezervari.getInt("sala");
                                                String filmSDB = resultSetselectALLRezervari.getString("film");
                                                Date dataSDB = resultSetselectALLRezervari.getDate("data");

                                                bufferedWriter.write("User: " + listOfElements.get(i).get(1) + "\t");
                                                bufferedWriter.newLine();
                                                bufferedWriter.write("Parola: " + listOfElements.get(i).get(2) + "\t");
                                                bufferedWriter.newLine();
                                                bufferedWriter.write("Rezervarea numarul: " + idRezervareSDB);
                                                bufferedWriter.newLine();
                                                bufferedWriter.write(formatInFrontOf + "NumeRezervare: " + numeRezevareSDB);
                                                bufferedWriter.newLine();
                                                bufferedWriter.write(formatInFrontOf + "Sala numarul: " + salaSDB);
                                                bufferedWriter.newLine();
                                                bufferedWriter.write(formatInFrontOf + "Filmul: " + filmSDB);
                                                bufferedWriter.newLine();
                                                bufferedWriter.write(formatInFrontOf + "Data: " + dataSDB);
                                                bufferedWriter.newLine();
                                                bufferedWriter.write(formatInFrontOf + "\n");

                                            }
                                        }

                                    }

                                    System.out.println("Informatiile din baza de date au fost salvate in fisierul: SalvareDb.txt ");

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                break;
                        }

                    } else {
                        System.out.println("Username sau parola gresita!\n" +
                                "Te rog sa incerci din nou.");
                    }

                    break;

                case "Exit":

                    System.out.println("Aplicatia se inchide.");
                    System.exit(200);

                    break;

                default:
                    System.out.println("Nu s-a ales o varianta corecta.");

            }

        }

    }


    public static int getSize(ResultSet resultSet) throws SQLException {
        int size = 0;
        if (resultSet != null) {
            resultSet.last();
            size = resultSet.getRow();
        }
        return size;
    }

    public static int getSize2(ResultSet resultSet) throws SQLException {
        List<String> item = new ArrayList<>();

        while (resultSet.next()) {
            String numeRezDelete = resultSet.getString("numeRezervare");
            item.add(numeRezDelete);
        }
        return item.size();
    }
}
