package kim.dongun.dotindicator

import android.os.Parcel
import android.os.Parcelable
import android.view.View.BaseSavedState
import org.jetbrains.annotations.NotNull

internal class SavedState : BaseSavedState {
  var count: Int = 0
  var selectedIndex = 0

  constructor(superState: Parcelable) : super(superState)

  private constructor(source: Parcel) : super(source) {
    this.count = source.readInt()
    this.selectedIndex = source.readInt()
  }

  override fun writeToParcel(out: Parcel, flags: Int) {
    super.writeToParcel(out, flags)
    out.writeInt(this.count)
    out.writeInt(this.selectedIndex)
  }

  companion object {
    @JvmField
    @NotNull
    val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
      override fun createFromParcel(source: Parcel): SavedState {
        return SavedState(source)
      }

      override fun newArray(size: Int): Array<SavedState?> {
        return arrayOfNulls(size)
      }
    }
  }
}