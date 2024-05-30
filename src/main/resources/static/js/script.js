const toggleSidebar = () => {
	if ($(".sidebar").is(":visible")) {
		// true
		// close sidebar
		$(".sidebar").css("display", "none");
		$(".content").css("margin-left", "0%");
	} else {
		// false
		// open sidebar
		$(".sidebar").css("display", "block");
		$(".content").css("margin-left", "20%");
	}
};

const search = () => {
	// console.log("searching");
	let query = $("#search-input").val();

	if (query == "") {
		$(".search-result").hide();
	} else {
		// search
		console.log(query);

		// sending request to server

		let url = `http://localhost:8080/search/${query}`;

		fetch(url)
			.then((response) => {
				return response.json();
			})
			.then((data) => {
				// data
				// console.log(data);

				let text = `<div class='list-group'>`;

				data.forEach((contact) => {
					text += `<a href='/user/contact/${contact.cId}' class='list-group-item list-group-item-action'> ${contact.name} </a>`;
				});

				text += `</div>`;

				$(".search-result").html(text);
				$(".search-result").show();
			});
	}
};

//First request to sever to create order

/*const paymentStart = () => {

	console.log("Payment started.");
	let amount = $("#payment_field").val();

	console.log(amount);

	if (amount == "" || amount == null) {
		// alert("amount is required !!");
		swal("Failed !!", "amount is required !!", "error");
		return;
	}

	//we will use Ajax to send request to server to create order - jquery Ajax full version(minified)

	$.ajax(
		{
			"url": '/user/create_order',
			"data": JSON.stringify({ amount: amount, info: 'order_request' }),
			"contentType": 'application/json',
			"type": 'POST',
			"dataType": 'json',
			"success": function(response) {
				//invoke when success
				console.log("open payment form")
				console.log(response);
				if (response.status == "created") {
					console.log("open payment form");
					//open payment form
					let options = {
						key: "rzp_test_KxOMePttMoP5hn",
						amount: response.amount,
						currency: "INR",
						name: "Smart Contact Manager",
						description: "Donation",
						image: "https://w7.pngwing.com/pngs/151/112/png-transparent-business-company-management-plan-industry-contact-us-company-service-people-thumbnail.png",
						order_id: response.id,
						handler: function(response) {
							console.log(response.razorpay_payment_id);
							console.log(response.razorpay_order_id);
							console.log(response.razorpay_signature);
							console.log("Payment successful..!");
							// alert("congrates !! Payment successful !!");
							swal("Congrats !", "congrates !! Payment successful !!", "success", {
								button: "Done !",
							});
						},

						prefill: {
							"name": "",
							"email": "",
							"contact": ""
						},
						notes: {
							"address": "SCM"
						},
						theme: {
							"color": "#3399cc"
						},
						method: {
							"upi": true,
							"card": true,
							"netbanking": true,
							"wallet": true
						}
					};

					var rzp = new Razorpay(options);
					rzp.on('payment.failed', function(response) {
						console.log(response.error.code);
						console.log(response.error.description);
						console.log(response.error.source);
						console.log(response.error.step);
						console.log(response.error.reason);
						console.log(response.error.metadata.order_id);
						console.log(response.error.metadata.payment_id);

						// alert(Oops payment failed !!");
						swal("Failed !", "Oops payment failed !!", "error", {
							button: "Done !",
						});
					});

					document.getElementById("clicked").onclick = function(e) {
						rzp.open();
					}
				}
			},
			"error": function(error) {
				//invoke when error
				console.log(error);
				alert("Something went wrong !!")
			}

		}

	)
}*/

const paymentStart2 = () => {
	console.log("Payment started.");
	let amount = $("#payment-field").val();
	console.log("Amount : ", amount);

	if (amount == "" || amount == null) {
		swal("Failed !!", "Amount is required !!", "error");
		return;
	}

	$.ajax({
		"url": '/user/create-order',
		"data": JSON.stringify({ amount: amount, info: 'order_request' }),
		"contentType": 'application/json',
		"type": 'POST',
		"dataType": 'json',
		"success": function (response) {
			console.log("Order created successfully:", response);
			if (response.status == "created") {
				let options = {
					key: "rzp_test_KxOMePttMoP5hn",
					amount: response.amount,
					currency: "INR",
					name: "Smart Contact Manager",
					description: "Donation",
					image: "https://scontent.fnag1-5.fna.fbcdn.net/v/t39.30808-6/371908688_2019099471796003_2276111562316110382_n.jpg?_nc_cat=105&ccb=1-7&_nc_sid=5f2048&_nc_ohc=rcTMHRj7_ZAQ7kNvgEmbzeU&_nc_ht=scontent.fnag1-5.fna&oh=00_AYA6f2W5tvheuvn_kSHa8aNrgTpZxWOzSaDNW1-yUOeqZQ&oe=6659CE9F",
					order_id: response.id,
					handler: function (response) {
						console.log("Payment successful:", response);
						swal("Congrats !", "Payment successful !!", "success", {
							button: "Done !",
						});
						$("#payment-field").val('');
					},
					prefill: {
						"name": "Apurrv",
						"email": "iamapurrv@gmail.com",
						"contact": ""
					},
					notes: {
						"address": "SCM"
					},
					theme: {
						"color": "#3399cc"
					},
					method: {
						"upi": true,
						"card": true,
						"netbanking": true,
						"wallet": true
					}
				};

				var rzp = new Razorpay(options);
				rzp.on('payment.failed', function (response) {
					console.log("Payment failed:", response);
					swal("Failed !", "Oops payment failed !!", "error", {
						button: "Done !",
					});
				});

				rzp.open();
			}
		},
		"error": function (error) {
			console.log("Error creating order:", error);
			alert("Something went wrong !!");
		}
	});
}
