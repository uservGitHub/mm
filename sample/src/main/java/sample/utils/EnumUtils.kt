package sample.utils

/**
 * Created by Administrator on 2018/2/18.
 */

enum class EditXY(val value:Byte) {
    X(0b01),
    Y(0b10),
    //Z(0b100),
    XY(0b11)
    //XYZ(0b111)
}
enum class EditDir(val value: Byte){
    Small(0b01),
    Big(0b10),
    Both(0b11)
}
enum class EditHand(val xy: EditXY,val dir: EditDir){
    Left(EditXY.X, EditDir.Big),
    Top(EditXY.Y, EditDir.Big),
    Right(EditXY.X, EditDir.Small),
    Bottom(EditXY.Y, EditDir.Small),
    //左上角
    LTCorner(EditXY.XY, EditDir.Big),
    //右下角
    RBCorner(EditXY.XY, EditDir.Small)
}