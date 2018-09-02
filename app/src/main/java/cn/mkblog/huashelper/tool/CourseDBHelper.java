package cn.mkblog.huashelper.tool;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cn.mkblog.huashelper.bean.CourseBean;

/**
 * 课程表数据库操作类
 */

public class CourseDBHelper extends SQLiteOpenHelper {
    public CourseDBHelper(Context context) {
        super(context, "timetable.db", null, 1);
    }

    /**
     * 查询指定键值是否存在，如果存在，则返回 ID，不存在则返回 0
     *
     * @param table 要查询的表
     * @param key   键值
     * @param value 值
     * @return ID
     */
    public int getKeyID(String table, String key, String value) {
        int id = 0;
        Cursor cursor = getReadableDatabase().query(table, null,
                key + "=?", new String[]{value}, null, null, null);
        if (cursor.moveToFirst()) { // 存在，获取 ID 值
            id = cursor.getInt(cursor.getColumnIndex("id"));
        }
        cursor.close();
        return id;
    }

    /**
     * 查询指定 ID 对应的名字
     *
     * @param table 要查询的表
     * @param id    ID值
     * @return 名字
     */
    public String getName(String table, String id) {
        String name = "";
        Cursor cursor = getReadableDatabase().query(table, null,
                "id=?", new String[]{id}, null, null, null);
        if (cursor.moveToFirst()) { // 存在，获取 ID 值
            name = cursor.getString(cursor.getColumnIndex("name"));
        }
        cursor.close();
        return name;
    }

    /**
     * 保存一个名称到指定表，不重复
     *
     * @param table 要插入的表
     * @param name  名字
     * @return 对应的键值ID
     */
    public int saveName(String table, String name) {
        int id = getKeyID(table, "name", name);
        if (id != 0) return id;

        ContentValues newVal = new ContentValues();
        newVal.put("name", name);

        return (int) getWritableDatabase().insert(table, null, newVal);
    }

    /**
     * 查询表中所有的名字(不返回空值)
     *
     * @param table 要查询的表
     * @return 包含所有名字值的字符串数组
     */
    public List<String> queryAllName(String table) {
        List<String> names = new ArrayList<>();

        Cursor cursor = getReadableDatabase().query(table, null,
                null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(cursor.getColumnIndex("name"));
                if (!name.equals("")) names.add(name);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return names;
    }

    /**
     * 删除一门课程
     *
     * @param oldID 要删除的课程的 ID 值
     * @return 无
     */
    public void deleteCourse(String oldID) {
        SQLiteDatabase rdb = getReadableDatabase();
        SQLiteDatabase wdb = getWritableDatabase();
        /**
         * 判断课程是否存在、删除课程
         * 教室信息是否依赖->删除
         * 教师信息是否依赖->删除
         * 课程名字是否依赖->删除
         * 删除所有周次信息
         * */
        Cursor cursor = rdb.query("timetable", null, "id=?", new String[]{oldID},
                null, null, null);

        if (cursor.moveToFirst()) { // 存在，获取教室、教师、课程名字的 ID 值
            String courseid = cursor.getString(cursor.getColumnIndex("courseid"));
            String teacherid = cursor.getString(cursor.getColumnIndex("teacherid"));
            String classroomid = cursor.getString(cursor.getColumnIndex("classroomid"));

            // 删除课程信息
            wdb.delete("timetable", "id=?", new String[]{oldID});

            // 删除之后判断课程名字是否还有使用，如果没有，则删除
            cursor = rdb.query("timetable", null, "courseid=?", new String[]{courseid},
                    null, null, null);

            if (!cursor.moveToFirst()) wdb.delete("course", "id=?", new String[]{courseid});


            // 判断教师是否还有使用，如果没有，则删除
            cursor = rdb.query("timetable", null, "teacherid=?", new String[]{teacherid},
                    null, null, null);

            if (!cursor.moveToFirst()) wdb.delete("teacher", "id=?", new String[]{teacherid});

            // 判断教室是否还有使用，如果没有，则删除
            cursor = rdb.query("timetable", null, "classroomid=?", new String[]{classroomid},
                    null, null, null);

            if (!cursor.moveToFirst()) wdb.delete("classroom", "id=?", new String[]{classroomid});

        }
        cursor.close();

        // 删除当前课程所有周次信息
        wdb.delete("coursetime", "classid=?", new String[]{oldID});
    }

    /**
     * 编辑课程 = 完全删除旧的课程信息 + 新增课程
     *
     * @param oldID     之前课程的 ID 值
     * @param name      课程名字
     * @param teacher   任课教师
     * @param classroom 教室地点
     * @param week      上课星期
     * @param section   上课节次
     * @param circle    上课周次
     * @return 无
     */
    public int editCourse(String oldID, String name, String teacher, String classroom, String week, String section, String circle) {
        deleteCourse(oldID);
        return addCourse(name, teacher, classroom, week, section, circle);
    }


    /**
     * 添加数据
     *
     * @param name      课程名字
     * @param teacher   任课教师
     * @param classroom 教室地点
     * @param week      上课星期
     * @param section   上课节次
     * @param circle    上课周次
     * @return 无
     */
    public int addCourse(String name, String teacher, String classroom, String week, String section, String circle) {
        SQLiteDatabase rdb = getReadableDatabase();
        SQLiteDatabase wdb = getWritableDatabase();

        String nid = String.valueOf(this.saveName("course", name));    // 记录课程名字并返回 ID
        String tid = String.valueOf(this.saveName("teacher", teacher));    // 记录教师名字并返回 ID
        String cid = String.valueOf(this.saveName("classroom", classroom));    // 记录教室名字并返回 ID

        // 插入课程信息到课程表
        String id;
        Cursor cursor = rdb.query("timetable", null,
                "courseid=? and teacherid=? and classroomid=? and week=? and section=?",
                new String[]{nid, tid, cid, week, section},
                null, null, null);
        if (cursor.moveToFirst()) {
            // 存在，获取 ID 值
            id = cursor.getString(cursor.getColumnIndex("id"));
        } else {
            // 不存在，插入新的并获取 ID
            ContentValues newVal = new ContentValues();
            newVal.put("courseid", nid);
            newVal.put("teacherid", tid);
            newVal.put("classroomid", cid);
            newVal.put("week", week);
            newVal.put("section", section);
            id = String.valueOf(getWritableDatabase().insert("timetable", null, newVal));
        }

        // 是否同一时间（星期、节次）还有其它课程
        String otherId = null;
        cursor = rdb.query("timetable", null,
                "week=? and section=?",
                new String[]{week, section},
                null, null, null);
        if (cursor.moveToFirst()) { // 存在，获取 ID 值
            otherId = cursor.getString(cursor.getColumnIndex("id"));
        }


        // 删除当前课程所有其它周次信息
        wdb.delete("coursetime", "classid=?", new String[]{id});

        // 循环周次插入
        // 按逗号解析周次信息并插入周次表
        String[] all = circle.split(",");
        for (String tmpCircle : all) {

            ContentValues newVal = new ContentValues();
            newVal.put("classid", id);
            newVal.put("circle", tmpCircle);

            // 如果有重合时段课程，把重合时段的课程周次ID 转过来，如果影响的行数为 0，代表这周没有重合（转过来）。需要插入
            if (otherId == null ||
                    wdb.update("coursetime", newVal, "circle=? and classid=?",
                            new String[]{tmpCircle, otherId}) <= 0) {

                Log.i("新插入", "1");
                wdb.insert("coursetime", null, newVal);
            }
        }

        // 看看重合的那个课程还有没有其它周，如果没有，那么删掉！
        cursor = rdb.query("coursetime", null,
                "classid=?",
                new String[]{otherId},
                null, null, null);
        if (!cursor.moveToFirst()) { // 没有了，删掉！
            deleteCourse(otherId);
            Log.i("新插入", "旧课程删除");
        }

        cursor.close();
        return 1;
    }

    /**
     * 获取所有的课程信息
     */
    public List<CourseBean> queryAll() {
        List<CourseBean> courseList = new ArrayList<>();

        // 参数依次是:表名，列名，where约束条件，where中占位符提供具体的值，指定group by的列，进一步约束
        // 指定查询结果的排序方式
        Cursor cursor = getReadableDatabase().query("timetable",
                null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                CourseBean courseInfo = new CourseBean();
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String courseid = cursor.getString(cursor.getColumnIndex("courseid"));
                String teacherid = cursor.getString(cursor.getColumnIndex("teacherid"));
                String classroomid = cursor.getString(cursor.getColumnIndex("classroomid"));
                int week = cursor.getInt(cursor.getColumnIndex("week"));
                int section = cursor.getInt(cursor.getColumnIndex("section"));

                courseInfo.setId(id);
                courseInfo.setName(getName("course", courseid));
                courseInfo.setTeacher(getName("teacher", teacherid));
                courseInfo.setClassroom(getName("classroom", classroomid));
                courseInfo.setWeek(week);
                courseInfo.setSection(section);

//                Log.i("课程信息", courseInfo.getId() + "\n" +
//                                courseInfo.getName() + "\n" +
//                                courseInfo.getTeacher() + "\n" +
//                                courseInfo.getClassroom() + "\n" +
//                                courseInfo.getWeek() + "\n" +
//                                courseInfo.getSection() + "\n"
//                );

                // 获取上课周次
                List<Integer> circleList = new ArrayList<>();
                Cursor time = getReadableDatabase().query("coursetime", null,
                        "classid=?", new String[]{String.valueOf(id)},
                        null, null, null);
                if (time.moveToFirst()) {
                    do {
                        circleList.add(time.getInt(time.getColumnIndex("circle")));
//                        Log.i("课程信息", time.getInt(time.getColumnIndex("circle")) + ",");
                    } while (time.moveToNext());
                }
                time.close();

                courseInfo.setCircle(circleList);

                courseList.add(courseInfo);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return courseList;
    }

    /**
     * 获取某周星期几的所有课程
     *
     * @param circle 周次
     * @param week   星期
     */
    public List<CourseBean> todayCourse(int circle, int week) {
        List<CourseBean> courseList = new ArrayList<>();

        // 参数依次是:表名，列名，where约束条件，where中占位符提供具体的值，指定group by的列，进一步约束
        // 指定查询结果的排序方式
        Cursor cursor = getReadableDatabase().query("timetable",
                null, "week=?", new String[]{String.valueOf(week)},
                null, null, "section");
        if (cursor.moveToFirst()) {
            do {
                CourseBean courseInfo = new CourseBean();
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String courseid = cursor.getString(cursor.getColumnIndex("courseid"));
                String teacherid = cursor.getString(cursor.getColumnIndex("teacherid"));
                String classroomid = cursor.getString(cursor.getColumnIndex("classroomid"));
                int section = cursor.getInt(cursor.getColumnIndex("section"));

                // 是否在自定上课周次
                Cursor time = getReadableDatabase().query("coursetime", null,
                        "classid=? and circle=?", new String[]{String.valueOf(id), String.valueOf(circle)},
                        null, null, null);
                if (time.moveToFirst()) {
                    courseInfo.setId(id);
                    courseInfo.setName(getName("course", courseid));
                    courseInfo.setTeacher(getName("teacher", teacherid));
                    courseInfo.setClassroom(getName("classroom", classroomid));
                    courseInfo.setSection(section);
                    courseList.add(courseInfo);
                }
                time.close();

            } while (cursor.moveToNext());
        }
        cursor.close();
        return courseList;
    }

    /**
     * 删除所有表中所有数据(慎用！你懂的。。)
     */
    public void deleteAll() {
        SQLiteDatabase wdb = getWritableDatabase();
        wdb.execSQL("delete from timetable");
        wdb.execSQL("delete from coursetime");
        wdb.execSQL("delete from course");
        wdb.execSQL("delete from teacher");
        wdb.execSQL("delete from classroom");
    }

    /* *************************************************************************************/

    /**
     * 添加数据
     *
     * @param nullColumnHack 空列的默认值
     * @param values         ContentValues类型的一个封装了列名称和列值的Map
     * @return 添加后 的 ID
     */
    public long insert(String table, String nullColumnHack, ContentValues values) {
        SQLiteDatabase wdb = getWritableDatabase();
        return wdb.insert(table, nullColumnHack, values);
    }

    /**
     * 更新数据
     *
     * @param values      更行列ContentValues类型的键值对（Map）
     * @param whereClause 更新条件（where字句）
     * @param whereArgs   更新条件数组
     * @return ？
     */
    public int update(String table, ContentValues values, String whereClause, String[] whereArgs) {
        SQLiteDatabase wdb = getWritableDatabase();
        return wdb.update(table, values, whereClause, whereArgs);
    }

    /**
     * 删除数据
     *
     * @param whereClause 删除条件
     * @param whereArgs   删除条件值数组
     * @return 删除数据条数
     */
    public int delete(String table, String whereClause, String[] whereArgs) {

        SQLiteDatabase wdb = getWritableDatabase();
        return wdb.delete(table, whereClause, whereArgs);
    }

    /**
     * 查询数据
     *
     * @param columns       要查询的字段
     * @param selection     要查询的条件
     * @param selectionArgs 要查询的条件中占位符的值
     * @param groupBy       对查询的结果进行分组
     * @param having        对分组的结果进行限制，分组条件
     * @param orderBy       对查询的结果进行排序（“id desc”表示根据id倒序）
     * @param limit         分页查询限制（如”1,3”表示获取第1到第3的数据共3条，
     *                      如“2”表示获取两条数据）
     * @return 查询结果
     */
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs,
                        String groupBy, String having, String orderBy, String limit) {

        SQLiteDatabase rdb = getReadableDatabase();
        return rdb.query(table, columns, selection, selectionArgs, groupBy,
                having, orderBy, limit);
    }

    /**
     * Cursor游标接口常用方法：
     * getCount()   总记录条数
     * isFirst()     判断是否第一条记录
     * isLast()      判断是否最后一条记录
     * moveToFirst()    移动到第一条记录
     * moveToLast()    移动到最后一条记录
     * move(int offset)   移动到指定记录
     * moveToNext()    移动到下一条记录
     * moveToPrevious()    移动到上一条记录
     * getColumnIndex(String columnName) 根据列名得到列位置id
     * getColumnIndexOrThrow(String columnName)  根据列名称获得列索引
     * getInt(int columnIndex)   获得指定列索引的int类型值
     * getString(int columnIndex)   获得指定列索引的String类型值
     */

    @Override
    // 初始化数据库表
    public void onCreate(SQLiteDatabase db) {
        // 课程表
        db.execSQL("CREATE TABLE timetable(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +      // 编号
                "courseid INTEGER, " +              // 课程名 ID
                "teacherid INTEGER, " +             // 教师名 ID
                "classroomid INTEGER," +         // 教室 ID
                "week INTEGER," +               // 星期
                "section INTEGER)");             // 节次

        // 时间表
        db.execSQL("CREATE TABLE coursetime(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +      // 编号
                "classid INTEGER, " +               // 对应的课程表编号
                "circle INTEGER)");                 // 第几周

        // 课程名表
        db.execSQL("CREATE TABLE course(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +      // 编号
                "name TEXT)");                      // 课程名字

        // 教师名表
        db.execSQL("CREATE TABLE teacher(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +      // 编号
                "name TEXT)");                      // 教师名字

        // 教室信息表
        db.execSQL("CREATE TABLE classroom(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +      // 编号
                "name TEXT)");                      // 教室名字

    }

    // 软件版本号发生改变时调用
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 数据库升级操作。。
    }
}
