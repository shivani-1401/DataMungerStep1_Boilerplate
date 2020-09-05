package com.stackroute.datamunger;
/*There are total 5 DataMungertest files:
 *
 * 1)DataMungerTestTask1.java file is for testing following 3 methods
 * a)getSplitStrings()  b) getFileName()  c) getBaseQuery()
 *
 * Once you implement the above 3 methods,run DataMungerTestTask1.java
 *
 * 2)DataMungerTestTask2.java file is for testing following 3 methods
 * a)getFields() b) getConditionsPartQuery() c) getConditions()
 *
 * Once you implement the above 3 methods,run DataMungerTestTask2.java
 *
 * 3)DataMungerTestTask3.java file is for testing following 2 methods
 * a)getLogicalOperators() b) getOrderByFields()
 *
 * Once you implement the above 2 methods,run DataMungerTestTask3.java
 *
 * 4)DataMungerTestTask4.java file is for testing following 2 methods
 * a)getGroupByFields()  b) getAggregateFunctions()
 *
 * Once you implement the above 2 methods,run DataMungerTestTask4.java
 *
 * Once you implement all the methods run DataMungerTest.java.This test case consist of all
 * the test cases together.
 */

public class DataMunger {

    /*
     * This method will split the query string based on space into an array of words
     * and display it on console
     */

    public String[] getSplitStrings(String queryString) {

        return queryString.toLowerCase().split(" ");
    }

    /*
     * Extract the name of the file from the query. File name can be found after a
     * space after "from" clause. Note: ----- CSV file can contain a field that
     * contains from as a part of the column name. For eg: from_date,from_hrs etc.
     *
     * Please consider this while extracting the file name in this method.
     */

    public String getFileName(String queryString) {
        String[] splitArray = getSplitStrings(queryString);
        String result = null;
        for (int i = 0; i < splitArray.length; i++) {
            if (splitArray[i].equals("from")) {
                result = splitArray[i + 1];
                break;
            }
        }
        return result;
    }


    /*
     * This method is used to extract the baseQuery from the query string. BaseQuery
     * contains from the beginning of the query till the where clause
     *
     * Note: ------- 1. The query might not contain where clause but contain order
     * by or group by clause 2. The query might not contain where, order by or group
     * by clause 3. The query might not contain where, but can contain both group by
     * and order by clause
     */

    public String getBaseQuery(String queryString) {
        String result = null;
        if (queryString.contains("where")) {
            result = queryString.substring(0, (queryString.indexOf("where") - 1));
        } else {
            if ((!queryString.contains("order by")) && (!queryString.contains("group by"))) {
                result = queryString;
            } else {
                int indexOfGroupBy = queryString.indexOf("group");
                int indexOfOrderBy = queryString.indexOf("order");
                if ((indexOfGroupBy != -1) && (indexOfOrderBy == -1)) {
                    result = queryString.substring(0, indexOfGroupBy - 1);
                } else if ((indexOfOrderBy != -1) && (indexOfGroupBy == -1)) {
                    result = queryString.substring(0, indexOfOrderBy - 1);
                } else {
                    int minIndex = indexOfGroupBy < indexOfOrderBy ? indexOfGroupBy : indexOfOrderBy;
                    result = queryString.substring(0, minIndex - 1);
                }

            }
        }
        return result;
    }

    /*
     * This method will extract the fields to be selected from the query string. The
     * query string can have multiple fields separated by comma. The extracted
     * fields will be stored in a String array which is to be printed in console as
     * well as to be returned by the method
     *
     * Note: 1. The field name or value in the condition can contain keywords
     * as a substring. For eg: from_city,job_order_no,group_no etc. 2. The field
     * name can contain '*'
     *
     */

    public String[] getFields(String queryString) {
        String[] result = {};
        if (queryString.toCharArray()[8] == '*') {
            result = null;
        } else {
            String requiredPart = "";
            int startIndex = 7;
            int indexOfFrom = queryString.indexOf("from");
            requiredPart = queryString.substring(startIndex, indexOfFrom - 1);
            result = requiredPart.split(",");
        }

        return result;
    }

    /*
     * This method is used to extract the conditions part from the query string. The
     * conditions part contains starting from where keyword till the next keyword,
     * which is either group by or order by clause. In case of absence of both group
     * by and order by clause, it will contain till the end of the query string.
     * Note:  1. The field name or value in the condition can contain keywords
     * as a substring. For eg: from_city,job_order_no,group_no etc. 2. The query
     * might not contain where clause at all.
     */

    public String getConditionsPartQuery(String queryString) {
        int len = queryString.length();
        String result = null;
        int indexOfWhere = queryString.indexOf("where");
        int indexOfGroup = queryString.indexOf("group by");
        int indexOfOrder = queryString.indexOf("order by");
        if (indexOfWhere != -1) {
            if ((indexOfGroup == -1) && (indexOfOrder == -1)) {
                result = queryString.toLowerCase().substring(indexOfWhere + 6, len);
            } else if ((indexOfGroup != -1) && (indexOfOrder == -1)) {
                result = queryString.toLowerCase().substring(indexOfWhere + 6, indexOfGroup - 1);
            } else if ((indexOfOrder != -1) && (indexOfGroup == -1)) {
                result = queryString.toLowerCase().substring(indexOfWhere + 6, indexOfOrder - 1);
            } else if ((indexOfOrder != -1) && (indexOfGroup != -1)) {
                int minIndex = indexOfGroup < indexOfOrder ? indexOfGroup : indexOfOrder;
                result = queryString.toLowerCase().substring(indexOfWhere + 6, minIndex - 1);
            }
        }
        return result;
    }

    /*
     * This method will extract condition(s) from the query string. The query can
     * contain one or multiple conditions. In case of multiple conditions, the
     * conditions will be separated by AND/OR keywords. for eg: Input: select
     * city,winner,player_match from ipl.csv where season > 2014 and city
     * ='Bangalore'
     *
     * This method will return a string array ["season > 2014","city ='bangalore'"]
     * and print the array
     *
     * Note: ----- 1. The field name or value in the condition can contain keywords
     * as a substring. For eg: from_city,job_order_no,group_no etc. 2. The query
     * might not contain where clause at all.
     */

    public String[] getConditions(String queryString) {
        String[] results = null;
        String wherePart = getConditionsPartQuery(queryString);
        if (wherePart != null) {
            results = wherePart.toLowerCase().replaceAll(" and ", " or ").split(" or ");
        }
        return results;
    }

    /*
     * This method will extract logical operators(AND/OR) from the query string. The
     * extracted logical operators will be stored in a String array which will be
     * returned by the method and the same will be printed Note:  1. AND/OR
     * keyword will exist in the query only if where conditions exists and it
     * contains multiple conditions. 2. AND/OR can exist as a substring in the
     * conditions as well. For eg: name='Alexander',color='Red' etc. Please consider
     * these as well when extracting the logical operators.
     *
     */

    public String[] getLogicalOperators(String queryString) {
        String whereCondition = getConditionsPartQuery(queryString);
        String result[] = null;
        if (queryString.contains("where")) {
            String[] splitArray = whereCondition.split(" ");
            int len = splitArray.length;
            StringBuilder temp = new StringBuilder();
            boolean andFlag = false;
            boolean orFlag = false;
            for (int i = 0; i < len; i++) {
                if (splitArray[i].equals("and") && andFlag == false) {
                    temp.append("and ");
                    andFlag = true;
                } else if (splitArray[i].equals("or") && orFlag == false) {
                    temp.append("or ");
                    orFlag = true;
                }

                if ((!temp.toString().equals(null)) && (temp.toString().length() != 0)) {
                    result = temp.toString().trim().split(" ");
                }
            }
        }
        return result;
    }

    /*
     * This method extracts the order by fields from the query string. Note:
     * 1. The query string can contain more than one order by fields. 2. The query
     * string might not contain order by clause at all. 3. The field names,condition
     * values might contain "order" as a substring. For eg:order_number,job_order
     * Consider this while extracting the order by fields
     */

    public String[] getOrderByFields(String queryString) {
        int indexOfOrder = queryString.indexOf("order by");
        int len = queryString.length();
        String[] result = null;
        String required = null;
        if (indexOfOrder != -1) {
            required = queryString.substring(indexOfOrder + 9, len);
            result = required.split(",");

        }

        return result;
    }

    /*
     * This method extracts the group by fields from the query string. Note:
     * 1. The query string can contain more than one group by fields. 2. The query
     * string might not contain group by clause at all. 3. The field names,condition
     * values might contain "group" as a substring. For eg: newsgroup_name
     *
     * Consider this while extracting the group by fields
     */

    public String[] getGroupByFields(String queryString) {
        int indexOfGroup = queryString.indexOf("group by");
        int indexOfOrder = queryString.indexOf("order by");
        int len = queryString.length();
        String[] result = null;
        String required = "";
        if (!(indexOfGroup == -1)) {
            if (!(indexOfOrder == -1)) {
                required = queryString.substring(indexOfGroup + 9, indexOfOrder - 1);
            } else {
                required = queryString.substring(indexOfGroup + 9, len);
            }
            result = required.trim().split(",");
        }
        return result;
    }

    /*
     * This method extracts the aggregate functions from the query string. Note:
     *  1. aggregate functions will start with "sum"/"count"/"min"/"max"/"avg"
     * followed by "(" 2. The field names might
     * contain"sum"/"count"/"min"/"max"/"avg" as a substring. For eg:
     * account_number,consumed_qty,nominee_name
     *
     * Consider this while extracting the aggregate functions
     */

    public String[] getAggregateFunctions(String queryString) {
        String result[] = null;
        String[] splitString = getSplitStrings(queryString);
        int lenSplitString = splitString.length;
        StringBuilder aggFun = new StringBuilder();
        for (int i = 0; i < lenSplitString; i++) {
            if (splitString[i].contains("(")) {
                aggFun.append(splitString[i]);
                break;
            }
        }
        if (!(aggFun.toString().equals(null)) && (aggFun.toString().length() != 0)) {
            result = aggFun.toString().split(",");
        }
        return result;

    }

}