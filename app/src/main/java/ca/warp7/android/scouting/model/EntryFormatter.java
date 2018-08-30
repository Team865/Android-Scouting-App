package ca.warp7.android.scouting.model;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Static Formatting Utilities for Entry
 *
 * @author Team 865
 */

public class EntryFormatter {

    /**
     * Formats the entry into a text report
     *
     * @param entry the entry to report on
     * @return a string to be displayed
     */
    public static String formatReport(Entry entry) {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm:ss", Locale.CANADA);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT-4"));
        StringBuilder sb = new StringBuilder();

        Specs specs = entry.getSpecs();

        sb
                .append('\n')

                .append(formatLeftWithSpaces("Match Number:", 16))
                .append(entry.getMatchNumber())
                .append('\n')

                .append(formatLeftWithSpaces("Team Number:", 16))
                .append(entry.getTeamNumber())
                .append('\n')

                .append(formatLeftWithSpaces("Start Time:", 16))
                .append(sdf.format(new Date(entry.getStartingTimestamp() * 1000L)))
                .append('\n')

                .append(formatLeftWithSpaces("Scouter:", 16))
                .append(entry.getScoutName())
                .append('\n')

                .append(formatLeftWithSpaces("Board:", 16))
                .append(specs.getBoardName())
                .append('\n')

                .append(formatLeftWithSpaces("Alliance:", 16))
                .append(specs.getAlliance())
                .append("\n\n")

                .append(formatLeftWithSpaces("Data", 24))
                .append("Value")
                .append("\n")
                .append(new String(new char[31]).replace("\0", "- "));

        for (EntryDatum d : entry.getDataStack()) {
            sb.append("\n");

            int t = d.getType();

            if (specs.hasIndexInConstants(t)) {
                DataConstant dc = specs.getDataConstantByIndex(t);
                sb
                        .append(formatLeftWithSpaces(dc.getLogTitle() +
                                (d.getStateFlag() == 0 ? "<Off>" : "") + " ", 24))
                        .append(dc.format(d.getValue()))
                        .append(d.getUndoFlag() != 0 ? " Ⓤ" : "");
            } else {
                sb
                        .append(formatLeftWithSpaces(String.valueOf(t), 21))
                        .append(String.valueOf(d.getValue()));
            }
        }

        sb.append('\n');

        if (!entry.getComments().isEmpty()) {
            sb
                    .append(new String(new char[31]).replace("\0", "*"))
                    .append('\n')
                    .append("New Comments: ")
                    .append(entry.getComments())
                    .append("\n");
        }

        sb.append(new String(new char[31]).replace("\0", "- "));

        return sb.toString();
    }

    /**
     * Formats the entry into hex encoded format
     *
     * @param entry the entry to encode
     * @return an encoded string, following an underscore-delimited convention
     */

    public static String formatEncode(Entry entry) {
        return formatHeader(entry) + "_" + formatDataCode(entry);
    }

    private static String formatHeader(Entry entry) {
        return entry.getMatchNumber() + "_" + entry.getTeamNumber() + "_" + entry.getScoutName();
    }

    private static String formatDataCode(Entry entry) {
        StringBuilder sb = new StringBuilder();
        sb
                .append(fillHex(entry.getStartingTimestamp(), 8))
                .append("_")
                .append(entry.getSpecs().getSpecsId())
                .append("_");

        for (EntryDatum d : entry.getDataStack())
            sb.append(fillHex(datumEncode(d), 4));

        sb.append("_");
        sb.append(entry.getComments());

        return sb.toString();
    }

    private static String formatRightWithZeroes(String s, int d) {
        return new String(new char[d - s.length()]).replace("\0", "0") + s;
    }

    private static String formatLeftWithSpaces(String s, int d) {
        return s + new String(new char[d - s.length()]).replace("\0", " ");
    }

    private static String fillHex(int n, int digits) {
        return formatRightWithZeroes(Integer.toHexString(n), digits);
    }

    private static int datumEncode(EntryDatum datum) {
        return datum.getUndoFlag() << 15
                | datum.getStateFlag() << 14
                | datum.getType() << 8
                | datum.getValue();
    }

}
