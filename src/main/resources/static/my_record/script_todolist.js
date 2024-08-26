    function editToDo(element) {
        var id = element.getAttribute("data-id");
        var description = element.getAttribute("data-description");

        document.getElementById("id").value = id;
        document.getElementById("description").value = description;

        document.getElementById("formTitle").innerText = "오늘의 목표 수정";
        document.getElementById("formButton").innerText = "업데이트";
    }

    function resetForm() {
        document.getElementById("id").value = "";
        document.getElementById("description").value = "";

        document.getElementById("formTitle").innerText = "오늘의 목표 추가";
        document.getElementById("formButton").innerText = "저장";
    }