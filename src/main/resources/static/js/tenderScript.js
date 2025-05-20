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
       
        if (selectedYear) {
            // Fetch months for the selected year (example)
            fetch(`/tender/years/${selectedYear}/months`)
                .then(response => response.json())
                .then(months => {

                    let monthOptions = "<option value=''>Choose Month</option>";
                    months.forEach(month => {
                        monthOptions += `<option value='${month}'>${month}</option>`;
                    });
                    document.getElementById("month-select").innerHTML = monthOptions;
                    document.getElementById("month-select").disabled = false; // Enable month dropdown
                });
        } else {
            document.getElementById("month-select").disabled = true; // Disable month dropdown
        }
    

    });




     // Fetch data and populate the "month-select" and "date-select" dropdowns
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

        if (selectedYear && selectedMonth) {
            // Fetch months for the selected year (example)
            fetch(`/tender/years/${selectedYear}/months/${selectedMonth}`)
                .then(response => response.json())
                .then(tenders  => {

                    const tenderList = document.getElementById("tender-items");
                    tenderList.innerHTML = "";
                    if (tenders.length === 0) {
                        tenderList.innerHTML = "<li>No tenders found for the selected month.</li>";
                    } else {
                        tenders.forEach(tender => {
                            const listItem = document.createElement("li");
                        
                            function createTenderLink(tender) {
                                const link = document.createElement("a");
                                link.href = `/tender/pdf/${tender.tid}`;
                                link.target = "_blank";
                                link.innerHTML = `${tender.title}`;
                        
                                // Fetch the actual PDF file size
                                fetch(`/tender/pdf/${tender.tid}`)
                                    .then(response => response.blob())
                                    .then(blob => {
                                        const fileSize = (blob.size / (1024 * 1024)).toFixed(2); // size in MB
                                        link.innerHTML += ` (File Size: ${fileSize} MB)`;
                                        link.innerHTML += ` <img src="/images/pdf-icon.png" alt="pdf-icon" style="height:25px; vertical-align:middle;">`;
                                    })
                                    .catch(error => {
                                        console.error("Error fetching file size:", error);
                                    });
                        
                                return link;
                            }
                        
                            const link = createTenderLink(tender);
                            listItem.appendChild(link);
                            tenderList.appendChild(listItem); 
                        });
                        
                        // tenders.forEach(tender  => {
                        //     console.log("tenders ", tenders);
                        //     const listItem = document.createElement("li");
                        //     listItem.innerHTML = `<a href="/tender/pdf/${tender.tid}" target="_blank">${tender.title}      <img src="/images/pdf-icon.png" alt="pdf-icon" style="height:25px;"></a>`;
                        //     console.log("tender details ", tender.tid, tender.title)
                        //     tenderList.appendChild(listItem);
                        // });
                    }
                })
                .catch(error => {
                    console.error("Error fetching tenders:", error);
                });
            }
            
    });