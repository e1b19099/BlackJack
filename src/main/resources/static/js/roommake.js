function numbercheck() {
  let cnt = document.getElementById("cnt").value;
  if (cnt > 4) {
    alert("人数は4人までです");
    return false;
  } else if (cnt <= 1) {
    alert("1人以下は入力できません");
    return false;
  } else {
    return true;
  }
}
