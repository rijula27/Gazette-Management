document.addEventListener('DOMContentLoaded', () => {
    // Menu toggle functionality
    const menuToggle = document.querySelector('.menu-toggle');
    const nav = document.querySelector('nav ul');
    
    if (menuToggle) {
        menuToggle.addEventListener('click', () => {
            nav.classList.toggle('active');
        });
    }


});




    // Fetch data and populate the "month-select" and "date-select" dropdowns
document.getElementById("year-select").addEventListener("change", function () {
    const selectedYear = this.value;
    // Clear previous selections in month and date dropdowns
    document.getElementById("month-select").innerHTML = "<option value=''>Choose Month</option>";
    document.getElementById("date-select").innerHTML = "<option value=''>Choose Date</option>";
    if (selectedYear) {
        // Fetch months for the selected year (example)
        fetch(`/gazette/years/${selectedYear}/months`)
            .then(response => response.json())
            .then(months => {
                const monthSelect = document.getElementById("month-select");

                // Clear existing options safely
                monthSelect.replaceChildren();

                // Default option
                const defaultOption = document.createElement("option");
                defaultOption.value = "";
                defaultOption.textContent = "Choose Month";
                monthSelect.appendChild(defaultOption);

                // Add month options safely
                months.forEach(month => {
                    const option = document.createElement("option");
                    option.value = month;
                    option.textContent = month;
                    monthSelect.appendChild(option);
                });

                monthSelect.disabled = false;
            });
    } else {
        document.getElementById("month-select").disabled = true; // Disable month dropdown
    }


    // if(month-select){
    //     fetch(`/gazette/years/${selectedYear}/months/$(month-select)/day`)
    // }
});



document.getElementById("month-select").addEventListener("change", function () {
    const selectedYear = document.getElementById("year-select").value;
    const selectedMonthName = this.value;

    
    console.log("selected year ", selectedYear , "selected month ", selectedMonthName )
    const monthMap = {
        "January": 1, "February": 2, "March": 3, "April": 4,
        "May": 5, "June": 6, "July": 7, "August": 8,
        "September": 9, "October": 10, "November": 11, "December": 12
    };
    const selectedMonth = monthMap[selectedMonthName];



    const dateSelect = document.getElementById("date-select");
    dateSelect.innerHTML = "<option value=''>Choose Date</option>";
    dateSelect.disabled = true;

    if (selectedYear && selectedMonth) {
        fetch(`/gazette/years/${selectedYear}/months/${selectedMonth}/dates`)
            .then(response => response.json())
            .then(dates => {
                console.log("Dates fetched:", dates);

                dateSelect.replaceChildren();

                const defaultOption = document.createElement("option");
                defaultOption.value = "";
                defaultOption.textContent = "Choose Date";
                dateSelect.appendChild(defaultOption);

                dates.forEach(date => {
                    const option = document.createElement("option");
                    option.value = date;
                    option.textContent = date;
                    dateSelect.appendChild(option);
                });

                dateSelect.disabled = false;
            });
    }
});




  // Fetch data and populate the "month-select" and "date-select" dropdowns
  document.getElementById("date-select").addEventListener("change", function () {
    const selectedYear = document.getElementById("year-select").value;
    const selectedMonthName = document.getElementById("month-select").value;
    const selectedDate = this.value;

    console.log("selected year ", selectedYear , "selected month ", selectedMonthName, "selected date ", selectedDate )
    const monthMap = {
        "January": 1, "February": 2, "March": 3, "April": 4,
        "May": 5, "June": 6, "July": 7, "August": 8,
        "September": 9, "October": 10, "November": 11, "December": 12
    };
    const selectedMonth = monthMap[selectedMonthName];



    if (selectedYear && selectedMonth && selectedDate) {
        // Fetch gazettes for the selected year, month, and date
        fetch(`/gazette/years/${selectedYear}/months/${selectedMonth}/dates/${selectedDate}`)
            .then(response => response.json())
            .then(gazettes => {
    
    
                const gazetteList = document.getElementById("gazette-items");
                // gazetteList.innerHTML = "";
                gazetteList.replaceChildren();
    
                if (gazettes.length === 0) {
                    // gazetteList.innerHTML = "<li>No gazettes found for the selected date.</li>";
                    gazetteList.replaceChildren();

                    const li = document.createElement("li");
                    li.textContent = "No gazettes found for the selected date.";
                    gazetteList.appendChild(li);
                } else {
                    gazettes.forEach(gazette => {
                        const listItem = document.createElement("li");
    
                        function createGazetteLink(gazette, partLabel, description) {
                            const link = document.createElement("a");
                            link.href = `/gazette/pdf/${gazette.id}`;
                            link.target = "_blank";
                            // link.innerHTML = `${partLabel} - ${description}`;
                            link.textContent = `${partLabel} - ${description}`;
    
                            // Fetch the actual PDF file size
                            fetch(`/gazette/pdf/${gazette.id}`)
                                .then(response => response.blob())
                                .then(blob => {
                                    const fileSize = (blob.size / (1024 * 1024)).toFixed(2); // size in MB
                                    // link.innerHTML += ` (File Size: ${fileSize} MB)`;
                                    // link.innerHTML += ` <img src="/images/pdf-icon.png" alt="pdf-icon" style="height:25px; vertical-align:middle;">`;
                                    link.textContent = `${partLabel} - ${description} (File Size: ${fileSize} MB) `;

                                    const img = document.createElement("img");
                                    img.src = "/images/pdf-icon.png";
                                    img.alt = "pdf-icon";
                                    img.style.height = "25px";
                                    img.style.verticalAlign = "middle";
                                    link.appendChild(img);
                                })
                                .catch(error => {
                                    console.error("Error fetching file size:", error);
                                });
    
                            return link;
                        }
    
                        let link = null;
    
                        switch (gazette.part) {
                            case "I":
                                link = createGazetteLink(gazette, "Part-I", "Appointments, Postings, Transfers, Powers, Leave and other Personal Notices");
                                break;
                            case "IIA":
                                link = createGazetteLink(gazette, "Part-IIA", "Resolution, Regulation, Orders Notification, Rules, etc., issued by the Local Government and Heads of the Departments");
                                break;
                            case "IIB":
                                link = createGazetteLink(gazette, "Part-IIB", "Orders, Notifications and Rules of the High Court of Meghalaya");
                                break;
                            case "III":
                                link = createGazetteLink(gazette, "Part-III", "Orders, Notifications, and Rules of the Government of India and by the Election Commission of India; papers extracted from the Gazette of India and other State Gazettes");
                                break;
                            case "IV":
                                link = createGazetteLink(gazette, "Part-IV", "Acts of the Legislative Assembly of Meghalaya and Ordinances promulgated by the Government of Meghalaya, Laws, Rules made by the Autonomous District Council");
                                break;
                            case "V":
                                link = createGazetteLink(gazette, "Part-V", "Bills introduced into the Legislative Assembly of Meghalaya");
                                break;
                            case "VI":
                                link = createGazetteLink(gazette, "Part-VI", "Proceedings of the Meghalaya Legislative Assembly");
                                break;
                            case "VII":
                                link = createGazetteLink(gazette, "Part-VII", "Acts and Parliament Ordinances Promulgated by the Parliament");
                                break;
                            case "VIII":
                                link = createGazetteLink(gazette, "Part-VIII", "Bills introduced in Parliament");
                                break;
                            case "IX":
                                link = createGazetteLink(gazette, "Part-IX", "Advertisements and Notices by the Government Offices, Public Bodies, and Affidavits");
                                break;
                            case "X":
                                link = createGazetteLink(gazette, "Extraordinary", " ");
                                break;
                            default:
                                link = createGazetteLink(gazette, gazette.part || "Unknown", "Unknown Gazette Part");
                                break;
                        }
    
                        if (link) {
                            listItem.appendChild(link);
                            gazetteList.appendChild(listItem);
                        }
                    });
                }
            })
            .catch(error => {
                console.error("Error fetching gazettes:", error);
                const gazetteList = document.getElementById("gazette-items");
                gazetteList.innerHTML = "<li>Failed to fetch gazettes. Please try again later.</li>";
            });
    }

  })    